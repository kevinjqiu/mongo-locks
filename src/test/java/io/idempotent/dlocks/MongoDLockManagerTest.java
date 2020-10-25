package io.idempotent.dlocks;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;


public class MongoDLockManagerTest {
    private static final String MONGO_CONNECTION_STR = "mongodb://localhost";
    private static final String DBNAME = "test";
    private static final String COLLNAME = "lock";
    private static final String OWNER1 = "OWNER1";
    private static final String OWNER2 = "OWNER2";

    private MongoDLockManager lm1;
    private MongoDLockManager lm2;

    @Before
    public void setup() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGO_CONNECTION_STR)).build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase(DBNAME);
        db.drop();

        lm1 = new MongoDLockManager(MONGO_CONNECTION_STR, DBNAME, COLLNAME, OWNER1);
        lm2 = new MongoDLockManager(MONGO_CONNECTION_STR, DBNAME, COLLNAME, OWNER2);
    }

    @Test
    public void cannotAcquireLockAfterLocked() {
        assertTrue(lm1.tryAcquire("foobar"));
        assertFalse(lm2.tryAcquire("foobar"));
    }

    @Test
    public void acquireAfterRelease() {
        assertTrue(lm1.tryAcquire("foobar"));
        lm1.release("foobar");
        assertTrue(lm2.tryAcquire("foobar"));
    }
}