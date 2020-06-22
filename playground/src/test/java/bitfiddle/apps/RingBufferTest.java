package bitfiddle.apps;

import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class RingBufferTest {

    @Test
    public void create_buffer_with_powOf2_capacity() {
        RingBuffer<String> buffer = new RingBuffer<>(10);
        assertEquals(16, buffer.capacity());
    }

    @Test
    public void when_1_element_added_then_size_returns_1() {
        RingBuffer<String> buffer = new RingBuffer<>(10);
        buffer.write("TEST");
        assertEquals(1, buffer.size());
    }

    @Test
    public void add_n_elements_and_retrieve_n_elements() {
        RingBuffer<String> buffer = new RingBuffer<>(4);
        buffer.write("V1");
        buffer.write("V2");
        buffer.write("V3");
        buffer.write("V4");

        assertEquals("V1", buffer.read());
        assertEquals("V2", buffer.read());
        assertEquals("V3", buffer.read());
        assertEquals("V4", buffer.read());
    }

    @Test
    public void rejects_element_when_buffer_is_full() {
        RingBuffer<String> buffer = new RingBuffer<>(4);
        IntStream.range(0, 4).forEach(x -> buffer.write("V" + x));
        assertEquals(false, buffer.write("V10"));
    }

    @Test
    public void read_returns_null_when_no_element_is_available() {
        RingBuffer<String> buffer = new RingBuffer<>(4);
        assertTrue(buffer.read() == null);
    }


}
