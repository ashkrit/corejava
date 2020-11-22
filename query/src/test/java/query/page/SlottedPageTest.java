package query.page;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlottedPageTest {


    @Test
    public void writeHeader() {
        SlotPage page = new SlotPage(1024, (byte) 1, 2);

        byte[] data = page.commit();

        ByteBuffer buffer = ByteBuffer.wrap(data);

        assertEquals(1, buffer.get(PageOffSets.PAGE_VERSION));
        assertEquals(2, buffer.getInt(PageOffSets.PAGE_NUMBER));
        assertEquals(0, buffer.getInt(PageOffSets.NO_OF_TUPLE));

        System.out.println(page);
    }

    @Test
    public void read_headers() {

        SlotPage expected = new SlotPage(1024, (byte) 1, 2);

        byte[] data = expected.commit();

        SlotPage actual = new SlotPage(data);

        assertEquals(actual.version(), expected.version());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(expected.noOfTuple(), expected.noOfTuple());

        System.out.println(actual);
    }

    @Test
    public void write_single_tuple() {

        SlotPage expected = new SlotPage(1024, (byte) 1, 2);

        expected.write("James".getBytes());

        byte[] data = expected.commit();

        SlotPage actual = new SlotPage(data);

        byte[] readBuffer = new byte[100];

        assertEquals(expected.noOfTuple(), expected.noOfTuple());
        assertEquals("James", new String(readBuffer, 0, actual.read(readBuffer)));

        System.out.println(actual);

    }

    @Test
    public void skip_read_when_reached_to_end_of_buffer() {

        SlotPage expected = new SlotPage(1024, (byte) 1, 2);


        expected.write("James".getBytes());

        byte[] data = expected.commit();

        SlotPage actual = new SlotPage(data);

        byte[] readBuffer = new byte[100];
        actual.read(readBuffer);

        assertEquals(-1, actual.read(readBuffer));

        System.out.println(actual);
    }

    @Test
    public void write_multiple_records() {
        SlotPage expected = new SlotPage(1024, (byte) 1, 2);

        expected.write("James".getBytes());
        expected.write("Bonds".getBytes());
        expected.write("Albert".getBytes());

        byte[] data = expected.commit();
        SlotPage actual = new SlotPage(data);
        byte[] readBuffer = new byte[100];

        assertEquals("James", new String(readBuffer, 0, actual.read(readBuffer)));
        assertEquals("Bonds", new String(readBuffer, 0, actual.read(readBuffer)));
        assertEquals("Albert", new String(readBuffer, 0, actual.read(readBuffer)));
        assertEquals(-1, actual.read(readBuffer));

        System.out.println(actual);
    }

    @Test
    public void handle_buffer_overflow() {
        SlotPage expected = new SlotPage(16, (byte) 1, 2);

        expected.write("AA".getBytes());

        assertEquals(-1, expected.write("test".getBytes()));

        System.out.println(expected);
    }

}
