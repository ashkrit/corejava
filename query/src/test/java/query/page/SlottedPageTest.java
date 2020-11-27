package query.page;

import org.junit.jupiter.api.Test;
import query.page.read.PageIterator;
import query.page.read.ReadPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class SlottedPageTest {


    @Test
    public void writeHeader() {
        WritePage page = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        byte[] data = page.commit();

        ByteBuffer buffer = ByteBuffer.wrap(data);

        assertEquals(1, buffer.get(PageOffSets.PAGE_VERSION));
        assertEquals(2, buffer.getInt(PageOffSets.PAGE_NUMBER));
        assertEquals(0, buffer.getInt(PageOffSets.NO_OF_TUPLE));

        System.out.println(page);
    }

    @Test
    public void read_headers() {

        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        byte[] data = expected.commit();

        ReadPage actual = ReadPage.create(data);

        assertEquals(actual.version(), expected.version());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(expected.noOfTuple(), expected.noOfTuple());

        System.out.println(actual);
    }

    @Test
    public void write_single_tuple() {

        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        expected.write("James".getBytes());

        byte[] data = expected.commit();

        ReadPage actual = ReadPage.create(data);

        byte[] readBuffer = new byte[100];

        assertEquals(expected.noOfTuple(), expected.noOfTuple());
        assertEquals("James", new String(readBuffer, 0, actual.newIterator().next(readBuffer)));

        System.out.println(actual);

    }

    @Test
    public void skip_read_when_reached_to_end_of_buffer() {

        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());


        expected.write("James".getBytes());

        byte[] data = expected.commit();

        ReadPage actual = ReadPage.create(data);

        byte[] readBuffer = new byte[100];
        PageIterator itr = actual.newIterator();
        itr.next(readBuffer);

        assertEquals(-1, itr.next(readBuffer));

        System.out.println(actual);
    }

    @Test
    public void write_multiple_records() {
        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        expected.write("James".getBytes());
        expected.write("Bonds".getBytes());
        expected.write("Albert".getBytes());

        byte[] data = expected.commit();
        ReadPage actual = ReadPage.create(data);

        byte[] readBuffer = new byte[100];
        PageIterator itr = actual.newIterator();

        assertEquals("James", new String(readBuffer, 0, itr.next(readBuffer)));
        assertEquals("Bonds", new String(readBuffer, 0, itr.next(readBuffer)));
        assertEquals("Albert", new String(readBuffer, 0, itr.next(readBuffer)));
        assertEquals(-1, itr.next(readBuffer));

        System.out.println(actual);
    }

    @Test
    public void handle_buffer_overflow() {
        WritableSlotPage expected = new WritableSlotPage(16, (byte) 1, 2, System.currentTimeMillis());

        expected.write("AA".getBytes());

        assertEquals(-1, expected.write("test".getBytes()));

        System.out.println(expected);
    }

    @Test
    public void read_using_iterator_like_interface() {
        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        expected.write("James".getBytes());
        expected.write("Bonds".getBytes());
        expected.write("Albert".getBytes());

        byte[] data = expected.commit();
        ReadPage page = ReadPage.create(data);
        byte[] readBuffer = new byte[100];

        List<String> records = new ArrayList<>();
        PageIterator itr = page.newIterator();
        while (itr.hasNext()) {
            int bytesRead = itr.next(readBuffer);
            records.add(new String(readBuffer, 0, bytesRead));
        }

        assertAll(
                () -> assertEquals(3, page.totalRecords()),
                () -> assertIterableEquals(asList("James", "Bonds", "Albert"), records)
        );

    }


    @Test
    public void access_record_by_index() {

        WritePage expected = new WritableSlotPage(1024, (byte) 1, 2, System.currentTimeMillis());

        expected.write("James".getBytes());
        expected.write("Bonds".getBytes());
        expected.write("Albert".getBytes());

        byte[] data = expected.commit();
        ReadPage page = ReadPage.create(data);
        byte[] readBuffer = new byte[100];

    }

}
