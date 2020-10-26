package io.idempotent.dlocks

import org.bson.codecs.pojo.annotations.BsonCreator
import org.bson.codecs.pojo.annotations.BsonProperty
import org.bson.types.ObjectId
import java.util.*

class MongoDLock @BsonCreator constructor(@BsonProperty("lockId") lockId: String,
                                          @BsonProperty("owner") owner: String,
                                          @BsonProperty("acquiredAt") acquiredAt: Date,
                                          @BsonProperty("renewedAt") renewedAt: Date,
                                          @BsonProperty("expiresAt") expiresAt: Date,
) {
    val id: ObjectId = ObjectId()
    val lockId: String = lockId
    val owner: String = owner
    val acquiredAt: Date = acquiredAt
    val renewedAt: Date = renewedAt
    val expiresAt: Date = expiresAt
}