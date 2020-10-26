package io.idempotent.dlocks

import java.time.Duration

interface DLockManager {
    fun tryAcquire(lockId: String): Boolean
    fun renew(lockId: String, duration: Duration)
    fun release(lockId: String)
}