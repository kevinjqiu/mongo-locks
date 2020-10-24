package io.idempotent.dlocks;

import java.time.Duration;

public interface DLock {
    boolean tryAcquire();
    void renew(Duration duration);
    void release();
}
