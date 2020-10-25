package io.idempotent.dlocks;

import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;


public class MongoDLockManagerTest {

    @Test
    public void simpleTest() {
        MongoDLockManager lockManager = new MongoDLockManager("mongodb://localhost", "test", "lock", "OWNER1");
        MongoDLock lock = lockManager.tryAcquire("foobar");
        System.out.println(lock);
    }
}