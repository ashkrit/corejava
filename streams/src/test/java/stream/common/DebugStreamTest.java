package stream.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class DebugStreamTest {

    @Test
    public void debug_using_peek() {
        List<Integer> values = Arrays.asList(1, 4, 5, 6);

        AtomicLong itemCount = new AtomicLong(0);// Used only for mutation
        int total = values
                .stream()
                .mapToInt(v -> v)
                .peek(x -> {
                    itemCount.incrementAndGet();
                    System.out.println("Received " + x);
                }) // Debug hook for streams
                .sum();

        Assertions.assertEquals(4, itemCount.get());


    }
}
