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

    private MongoDLockManager lm1;
    private MongoDLockManager lm2;

    @Before
    public void setup() {
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(MONGO_CONNECTION_STR)).build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase db = mongoClient.getDatabase("test");
        db.drop();

        lm1 = new MongoDLockManager(MONGO_CONNECTION_STR, "test", "lock", "OWNER1");
        lm2 = new MongoDLockManager(MONGO_CONNECTION_STR, "test", "lock", "OWNER2");
    }

    @Test
    public void simpleTest() {
        assertTrue(lm1.tryAcquire("foobar"));
        assertFalse(lm2.tryAcquire("foobar"));
    }
}