package io.idempotent.dlocks;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import java.time.Duration;

public class MongoDLock implements DLock, AutoCloseable {
    private final MongoClient mongoClient;
    private final MongoDatabase db;
    private final MongoCollection<MongoDLockDoc> coll;

    public MongoDLock(String mongoConnectionStr, String dbName, String collName) {
        mongoClient = MongoClients.create(mongoConnectionStr);
        db = mongoClient.getDatabase(dbName);
        coll = db.getCollection(collName, MongoDLockDoc.class);
    }

    public boolean tryAcquire() {
        return false;
    }

    public void renew(Duration duration) {

    }

    public void release() {

    }

    @Override
    public void close() throws Exception {
        mongoClient.close();
    }
}
