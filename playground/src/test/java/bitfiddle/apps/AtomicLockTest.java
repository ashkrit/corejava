package bitfiddle.apps;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AtomicLockTest {

    @Test
    public void request_successful_lock() {
        AtomicLock atomicLock = new AtomicLock();
        Assertions.assertEquals(true, atomicLock.lock(1));
    }

    @Test
    public void request_successful_lock_release_lock() {
        AtomicLock atomicLock = new AtomicLock();
        atomicLock.lock(1);
        Assertions.assertEquals(true, atomicLock.release(1));
    }


    @Test
    public void fail_when_lock_is_not_available() {
        AtomicLock atomicLock = new AtomicLock();
        atomicLock.lock(5);
        Assertions.assertEquals(false, atomicLock.lock(5));

    }
}
