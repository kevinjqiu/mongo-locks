package io.idempotent.dlocks;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.time.Duration;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

public class MongoDLockManager implements DLockManager, AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoCollection<MongoDLock> coll;
    private final String ownerId;

    public MongoDLockManager(String mongoConnectionStr, String dbName, String collName, String ownerId) {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        MongoClientSettings settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(new ConnectionString(mongoConnectionStr)).build();
        mongoClient = MongoClients.create(settings);
        db = mongoClient.getDatabase(dbName);
        coll = db.withReadConcern(ReadConcern.LINEARIZABLE)
                .withWriteConcern(WriteConcern.MAJORITY)
                .getCollection(collName, MongoDLock.class);
        ensureIndex();

        this.ownerId = ownerId;
    }

    private void ensureIndex() {
        IndexOptions options = new IndexOptions();
        options.expireAfter(10L, TimeUnit.MINUTES);
        coll.createIndex(Indexes.ascending("renewedAt"), options);
    }

    public boolean tryAcquire(String lockId) {
        Date now = new Date();
        MongoDLock lockDoc = new MongoDLock(lockId, ownerId, now, now);

        InsertOneResult result = coll.insertOne(lockDoc);
        return result.wasAcknowledged();
    }

    @Override
    public void renew(Duration duration) {

    }

    @Override
    public void release() {

    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
