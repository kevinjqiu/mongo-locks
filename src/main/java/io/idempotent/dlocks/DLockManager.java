package io.idempotent.dlocks;

public interface DLockManager<T extends DLock> {
    T tryAcquire(String lockId);
}
