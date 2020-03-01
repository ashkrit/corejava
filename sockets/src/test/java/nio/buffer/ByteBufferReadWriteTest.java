package nio.buffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferReadWriteTest {

    @Test
    @DisplayName("Write to buffer and read using naive way" )
    public void writeBuffer() {
        /*
        [0,0,0,0,0,0,0,0,0,0]
         */
        var buffer = ByteBuffer.allocate(10);
        buffer
                .put((byte) 10)
                .put((byte) 20)
                .put((byte) 30);

        /*
        [10,20,30,0,0,0,0,0,0,0]
         */

        assertEquals(3, buffer.position());
        assertEquals(7, buffer.remaining());

        assertEquals(10, buffer.get(0));
        assertEquals(20, buffer.get(1));
        assertEquals(30, buffer.get(2));
    }

    @Test
    @DisplayName("Read using flip" )
    public void readUsingFlip() {

        var buffer = ByteBuffer.allocate(10);
        buffer
                .put((byte) 10)
                .put((byte) 20)
                .put((byte) 30);

        /*
             P - Position,L - Limit
            [10,20,30,0,0,0,0,0,0,0]
                      P           L

         */

        buffer.flip();

        /*
            P - Position,L - Limit
            [10,20,30,0,0,0,0,0,0,0]
             P        L

         */

        assertEquals(3, buffer.remaining());
        assertEquals(10, buffer.get());
        assertEquals(20, buffer.get());
        assertEquals(30, buffer.get());
        assertEquals(0, buffer.remaining());
    }

    @Test
    @DisplayName("Multiple read/write using flip" )
    public void multipleReadWrite() {

        var buffer = ByteBuffer.allocate(10);
        buffer
                .put((byte) 10)
                .put((byte) 20)
                .put((byte) 30);

        buffer.flip(); // Make it ready to read
        while (buffer.hasRemaining()) {
            buffer.get();
        }

        buffer.flip();//Make it ready for write
        buffer.clear();
        range(0, 10).forEach(b -> buffer.put((byte) b));
        buffer.flip();
        byte b = 0;
        while (buffer.hasRemaining()) {
            assertEquals(b++, buffer.get());
        }

    }

}
