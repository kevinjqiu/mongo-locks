package io.idempotent.dlocks;


import java.time.Duration;

public interface DLock {
    void release();
    void renew(Duration duration);
}
