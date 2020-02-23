package nio.buffer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ByteBufferLearningTest {

    @Test
    @DisplayName("Check properties of newly created buffer" )
    public void newlyCreatedBuffer() {

        var buffer = ByteBuffer.allocate(10);

        assertEquals(10, buffer.capacity());
        assertEquals(10, buffer.limit());
        assertEquals(10, buffer.remaining());

        assertEquals(0, buffer.position());

        assertEquals(buffer, buffer.mark());
    }


    @Test
    @DisplayName("Read Write buffer" )
    public void defaultBufferSupportsReadAndWrite() {
        assertEquals(false, ByteBuffer.allocate(10).isReadOnly());
    }

    @Test
    @DisplayName("Change limit of newly allocated buffer" )
    public void changeLimitOfPreallocatedBuffer() {

        var buffer = ByteBuffer.allocate(10);

        buffer.limit(5);

        assertEquals(10, buffer.capacity());
        assertEquals(5, buffer.limit());
        assertEquals(5, buffer.remaining());

    }

    @Test
    @DisplayName("Change in position in buffer adjust remaining capacity" )
    public void bufferPos() {
        var buffer = ByteBuffer.allocate(10);
        buffer.position(3);

        assertEquals(10, buffer.capacity());
        assertEquals(10, buffer.limit());
        assertEquals(3, buffer.position());
        assertEquals(7, buffer.remaining());

    }


    @Test
    @DisplayName("Sharing of buffer as buffer view" )
    public void bufferViews() {

        var buffer = ByteBuffer.allocate(10);
        var stage1 = buffer.duplicate().position(0).limit(5);

        var stage2 = buffer.duplicate().position(5);


        assertEquals(10, stage1.capacity());
        assertEquals(0, stage1.position());
        assertEquals(5, stage1.remaining());


        assertEquals(10, stage2.capacity());
        assertEquals(5, stage2.position());
        assertEquals(5, stage2.remaining());
    }


}
