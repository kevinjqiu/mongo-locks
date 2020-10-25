package io.idempotent.dlocks;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.*;
import com.mongodb.client.result.InsertOneResult;
import org.bson.*;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDLockManager implements DLockManager, AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoCollection<MongoDLock> coll;
    private final String ownerId;

    public MongoDLockManager(String mongoConnectionStr, String dbName, String collName, String ownerId) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(mongoConnectionStr)).build();
        mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase(dbName);
        coll = db.withReadConcern(ReadConcern.LINEARIZABLE)
                .withWriteConcern(WriteConcern.MAJORITY)
                .getCollection(collName, MongoDLock.class);
        ensureIndex();

        this.ownerId = ownerId;
    }

    private void ensureIndex() {
        IndexOptions options = new IndexOptions();
        options.expireAfter(10L, TimeUnit.MINUTES);
        options.unique(true);
        BsonDocument index = new BsonDocument()
                .append("lockId", new BsonInt32(1));

        coll.createIndex(index, options);
    }

    public boolean tryAcquire(String lockId) {
        Date now = new Date();

        MongoDLock lockDoc = new MongoDLock(lockId, ownerId, now, now);
        try {
            InsertOneResult result = coll.insertOne(lockDoc);
            return result.wasAcknowledged();
        } catch (com.mongodb.MongoWriteException e) {
            if (e.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Unable to acquire lock");
            }
            return false;
        }
    }

    @Override
    public void renew(String lockId, Duration duration) {

    }

    @Override
    public void release(String lockId) {

    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
