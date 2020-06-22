package bitfiddle.apps;

import bitfiddle.Bits;
import bitfiddle.MoreInts;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/*
    This is using single Int to manage 32 locks in thread safe way. This has less memory usage as compared to JDK lock which uses Int to manage single lock.
 */

public class Locks {
    public static final int INT_BYTES = 32;
    private AtomicInteger lock = new AtomicInteger(0);

    public boolean lock(int index) {
        int value = lock.get();
        if (Bits.isSet(value, index)) {
            return false;
        }
        int newLock = Bits.set(value, index);
        return lock.compareAndSet(value, newLock);
    }

    public boolean release(int index) {
        int value = lock.get();
        int newLock = Bits.clear(value, index);
        return lock.compareAndSet(value, newLock);
    }

    public void prettyPrint() {
        int value = lock.get();
        System.out.println(pad(MoreInts.toBinary(value)));
    }

    private String pad(String s) {
        int pad = INT_BYTES - s.length();
        return s.length() > 31 ? s.substring(s.length() - INT_BYTES) : lPad(s, pad);
    }

    private static String lPad(String s, int pad) {
        return IntStream.range(0, pad)
                .mapToObj(x -> "0")
                .collect(Collectors.joining()) + s;
    }

}
