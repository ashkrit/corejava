package stream.ch006.morecollectors.queue;

import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DequeueTest {

    @Test
    public void verify_double_ended_queue_operations() {
        Deque<String> queue = new ArrayDeque<>();

        queue.add("v1");
        queue.add("v2");
        queue.add("v3");

        assertEquals("v1", queue.getFirst());
        assertEquals("v3", queue.getLast());

        queue.removeLast();
        assertEquals("v2", queue.getLast());

    }
}
