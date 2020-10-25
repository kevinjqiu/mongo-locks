package io.idempotent.dlocks;

import java.time.Duration;

public interface DLockManager {
    boolean tryAcquire(String lockId);
    void renew(String lockId, Duration duration);
    void release(String lockId);
}
