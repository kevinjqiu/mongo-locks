package io.idempotent.dlocks;

import org.bson.codecs.pojo.annotations.BsonCreator;
import org.bson.codecs.pojo.annotations.BsonProperty;
import org.bson.types.ObjectId;

import java.util.Date;

public class MongoDLock {
    private final ObjectId id;
    private final String lockId;
    private final String owner;
    private final Date acquiredAt;
    private final Date renewedAt;

    @BsonCreator
    public MongoDLock(@BsonProperty("lockId") final String lockId,
                      @BsonProperty("owner") final String owner,
                      @BsonProperty("acquiredAt") final Date acquiredAt,
                      @BsonProperty("renewedAt") final Date renewedAt) {
        this.id = new ObjectId();
        this.lockId = lockId;
        this.owner = owner;
        this.acquiredAt = acquiredAt;
        this.renewedAt = renewedAt;
    }

    public ObjectId getId() {
        return id;
    }

    public String getLockId() {
        return lockId;
    }

    public String getOwner() {
        return owner;
    }

    public Date getAcquiredAt() {
        return acquiredAt;
    }

    public Date getRenewedAt() {
        return renewedAt;
    }
}
