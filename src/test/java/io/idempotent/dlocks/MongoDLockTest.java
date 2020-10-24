package io.idempotent.dlocks;

import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;


public class MongoDLockTest {

    @Test
    public void simpleTest() {
        DLock lock = new MongoDLock("mongodb://localhost", "test", "lock");
        assertNotNull(lock);
    }

}