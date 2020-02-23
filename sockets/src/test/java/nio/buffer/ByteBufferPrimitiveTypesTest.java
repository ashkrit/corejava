package nio.buffer;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.LongBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferPrimitiveTypesTest {

    @Test
    public void longBuffers() {
        var buffer = LongBuffer.allocate(5);
        buffer.put(10).put(20).put(30);

        buffer.flip();
        assertEquals(10, buffer.get());
        assertEquals(20, buffer.get());
        assertEquals(30, buffer.get());
    }

    @Test
    public void longDirectBuffers() {
        var buffer = ByteBuffer.allocateDirect(8 * 5).asLongBuffer();
        buffer.put(10).put(20).put(30);

        buffer.flip();
        assertEquals(10, buffer.get());
        assertEquals(20, buffer.get());
        assertEquals(30, buffer.get());

        assertEquals(10, buffer.get(0));
        assertEquals(20, buffer.get(1));
        assertEquals(30, buffer.get(2));

    }

}
