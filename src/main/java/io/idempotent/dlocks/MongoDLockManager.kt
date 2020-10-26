package io.idempotent.dlocks

import com.mongodb.*
import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients
import com.mongodb.client.MongoCollection
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.InsertOneOptions
import org.bson.BsonDocument
import org.bson.BsonInt32
import org.bson.BsonString
import org.bson.codecs.configuration.CodecRegistries
import org.bson.codecs.pojo.PojoCodecProvider
import org.bson.conversions.Bson
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit

class MongoDLockManager(mongoConnectionStr: String, dbName: String, collName: String, ownerId: String) : DLockManager, AutoCloseable {
    private val mongoClient: MongoClient
    private val coll: MongoCollection<MongoDLock>
    private val ownerId: String
    private fun ensureIndex() {
        val options = IndexOptions()
        options.expireAfter(10L, TimeUnit.MINUTES)
        options.unique(true)
        val index = BsonDocument()
                .append("lockId", BsonInt32(1))
        coll.createIndex(index, options)
    }

    override fun tryAcquire(lockId: String): Boolean {
        val now = Date()
        val lockDoc = MongoDLock(lockId, ownerId, now, now, now)
        return try {
            val result = coll.insertOne(lockDoc)
            result.wasAcknowledged()
        } catch (e: MongoWriteException) {
            if (e.error.category == ErrorCategory.DUPLICATE_KEY) {
                throw AlreadyLocked()
            }
            throw AcquireLockException(e)
        }
    }

    override fun renew(lockId: String, duration: Duration) {}
    override fun release(lockId: String) {
        val filter: Bson = BsonDocument()
                .append("lockId", BsonString(lockId))
                .append("owner", BsonString(ownerId))
        coll.deleteMany(filter)
    }

    override fun close() {
        mongoClient.close()
    }

    init {
        val pojoCodecRegistry = CodecRegistries.fromRegistries(MongoClientSettings.getDefaultCodecRegistry(),
                CodecRegistries.fromProviders(PojoCodecProvider.builder().automatic(true).build()))
        val settings = MongoClientSettings.builder()
                .codecRegistry(pojoCodecRegistry)
                .applyConnectionString(ConnectionString(mongoConnectionStr)).build()
        mongoClient = MongoClients.create(settings)
        val db = mongoClient.getDatabase(dbName)
        coll = db.withReadConcern(ReadConcern.LINEARIZABLE)
                .withWriteConcern(WriteConcern.MAJORITY)
                .getCollection(collName, MongoDLock::class.java)
        ensureIndex()
        this.ownerId = ownerId
    }
}