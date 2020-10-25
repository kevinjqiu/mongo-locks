package io.idempotent.dlocks;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


public class MongoDLockManagerTest {

    @Test
    public void simpleTest() {
        MongoDLockManager lockManager = new MongoDLockManager("mongodb://localhost", "test", "lock", "OWNER1");
        assertTrue(lockManager.tryAcquire("foobar"));
//        System.out.println(lock);
    }
}