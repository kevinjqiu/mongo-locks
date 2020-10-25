package io.idempotent.dlocks;

import org.bson.types.ObjectId;

import java.time.Duration;
import java.util.Date;

public class MongoDLock implements DLock {
    private ObjectId id;
    private String owner;
    private Date acquiredAt;
    private Date renewedAt;

    public MongoDLock(ObjectId id, String owner, Date acquiredAt, Date renewedAt) {
        this.id = id;
        this.owner = owner;
        this.acquiredAt = acquiredAt;
        this.renewedAt = renewedAt;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Date getAcquiredAt() {
        return acquiredAt;
    }

    public void setAcquiredAt(Date acquiredAt) {
        this.acquiredAt = acquiredAt;
    }

    public Date getRenewedAt() {
        return renewedAt;
    }

    public void setRenewedAt(Date renewedAt) {
        this.renewedAt = renewedAt;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    @Override
    public void release() {

    }

    @Override
    public void renew(Duration duration) {

    }
}
