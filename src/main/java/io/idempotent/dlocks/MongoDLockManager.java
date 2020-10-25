package io.idempotent.dlocks;

import com.mongodb.ReadConcern;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import com.mongodb.client.result.InsertOneResult;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.concurrent.TimeUnit;

public class MongoDLockManager implements DLockManager<MongoDLock>, AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoCollection<MongoDLock> coll;
    private final String ownerId;

    public MongoDLockManager(String mongoConnectionStr, String dbName, String collName, String ownerId) {
        mongoClient = MongoClients.create(mongoConnectionStr);
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

    public MongoDLock tryAcquire(String lockId) {
        Date now = new Date();
        MongoDLock lockDoc = new MongoDLock(new ObjectId(lockId), ownerId, now, now);

        InsertOneResult result = coll.insertOne(lockDoc);
        if (!result.wasAcknowledged()) {
            return null;
        }
        return lockDoc;
    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
