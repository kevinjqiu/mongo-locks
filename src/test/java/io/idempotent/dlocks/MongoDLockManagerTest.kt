package io.idempotent.dlocks

import com.mongodb.ConnectionString
import com.mongodb.MongoClientSettings
import com.mongodb.client.MongoClients
import junit.framework.TestCase
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class MongoDLockManagerTest {
    private var lm1: MongoDLockManager? = null
    private var lm2: MongoDLockManager? = null
    @Before
    fun setup() {
        val settings = MongoClientSettings.builder()
                .applyConnectionString(ConnectionString(MONGO_CONNECTION_STR)).build()
        val mongoClient = MongoClients.create(settings)
        val db = mongoClient.getDatabase(DBNAME)
        db.drop()
        lm1 = MongoDLockManager(MONGO_CONNECTION_STR, DBNAME, COLLNAME, OWNER1)
        lm2 = MongoDLockManager(MONGO_CONNECTION_STR, DBNAME, COLLNAME, OWNER2)
    }

    @Test
    fun cannotAcquireLockAfterLocked() {
        TestCase.assertTrue(lm1!!.tryAcquire("foobar"))
        Assert.assertFalse(lm2!!.tryAcquire("foobar"))
    }

    @Test
    fun acquireAfterRelease() {
        TestCase.assertTrue(lm1!!.tryAcquire("foobar"))
        lm1!!.release("foobar")
        TestCase.assertTrue(lm2!!.tryAcquire("foobar"))
    }

    companion object {
        private const val MONGO_CONNECTION_STR = "mongodb://localhost"
        private const val DBNAME = "test"
        private const val COLLNAME = "lock"
        private const val OWNER1 = "OWNER1"
        private const val OWNER2 = "OWNER2"
    }
}