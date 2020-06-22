package bitfiddle.apps;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class LocksTest {

    @Test
    public void request_successful_lock() {
        Locks locks = new Locks();
        Assertions.assertEquals(true, locks.lock(1));
    }

    @Test
    public void request_successful_lock_release_lock() {
        Locks locks = new Locks();
        locks.lock(1);
        Assertions.assertEquals(true, locks.release(1));
    }


    @Test
    public void fail_when_lock_is_not_available() {
        Locks locks = new Locks();
        locks.lock(5);
        Assertions.assertEquals(false, locks.lock(5));

    }
}
