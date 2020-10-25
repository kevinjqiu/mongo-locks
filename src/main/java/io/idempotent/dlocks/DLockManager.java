package io.idempotent.dlocks;

import java.time.Duration;

public interface DLockManager {
    boolean tryAcquire(String lockId);
    void renew(Duration duration);
    void release();
}
