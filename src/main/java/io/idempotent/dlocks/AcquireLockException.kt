package io.idempotent.dlocks

import java.lang.Exception

class AcquireLockException(cause: Throwable) : Exception(cause)