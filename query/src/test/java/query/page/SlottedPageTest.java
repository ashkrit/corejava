package query.page;

import org.junit.jupiter.api.Test;
import query.page.read.PageIterator;
import query.page.read.ReadPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;

public class SlottedPageTest {

    byte version = (byte) 1;

    @Test
    public void writeHeader() {

        WritePage page = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());

        byte[] data = page.commit();

        ByteBuffer buffer = ByteBuffer.wrap(data);

        assertEquals(1, buffer.get(PageOffSets.PAGE_VERSION));
        assertEquals(2, buffer.getInt(PageOffSets.PAGE_NUMBER));
        assertEquals(0, buffer.getInt(PageOffSets.NO_OF_TUPLE));

        System.out.println(page);
    }

    @Test
    public void read_headers() {

        WritePage expected = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());

        byte[] data = expected.commit();

        ReadPage actual = ReadPage.create(data);

        assertEquals(actual.version(), expected.version());
        assertEquals(actual.pageNumber(), expected.pageNumber());
        assertEquals(expected.noOfTuple(), expected.noOfTuple());

        System.out.println(actual);
    }

    @Test
    public void write_single_tuple() {

        WritePage expected = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());

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

        WritePage expected = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());


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
        WritePage expected = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());

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
        WritableSlotPage expected = new WritableSlotPage(16, version, 2, System.currentTimeMillis());

        expected.write("AA".getBytes());

        assertEquals(-1, expected.write("test".getBytes()));

        System.out.println(expected);
    }

    @Test
    public void read_using_iterator_like_interface() {
        WritePage expected = new WritableSlotPage(1024, version, 2, System.currentTimeMillis());

        expected.write("James".getBytes());
        expected.write("Bonds".getBytes());
        expected.write("Albert".getBytes());

        byte[] data = expected.commit();
        ReadPage page = ReadPage.create(data);
        byte[] readBuffer = new byte[100];


        List<String> records = collect(readBuffer, page.newIterator());

        assertAll(
                () -> assertEquals(3, page.totalRecords()),
                () -> assertIterableEquals(asList("James", "Bonds", "Albert"), records)
        );

    }

    public List<String> collect(byte[] readBuffer, PageIterator itr) {
        List<String> records = new ArrayList<>();
        while (itr.hasNext()) {
            int bytesRead = itr.next(readBuffer);
            records.add(new String(readBuffer, 0, bytesRead));
        }
        return records;
    }


    @Test
    public void support_multiple_iterator() {

        WritePage expected = new WritableSlotPage(1024 * 64, version, 2, System.currentTimeMillis());

        IntStream.range(0, 1000).forEach(i -> {
            expected.write(("James" + i).getBytes());
        });

        byte[] data = expected.commit();
        ReadPage page = ReadPage.create(data);

        ExecutorService es = Executors.newFixedThreadPool(2);

        Future<List<String>> r1 = es.submit(() -> collect(new byte[100], page.newIterator()));
        Future<List<String>> r2 = es.submit(() -> collect(new byte[100], page.newIterator()));

        assertAll(
                () -> assertEquals(r1.get(), r2.get())
        );

    }

    @Test
    public void retrieve_record_by_offset() {

        WritePage expected = new WritableSlotPage(1024 * 64, version, 2, System.currentTimeMillis());
        IntStream.range(0, 1000).forEach(i -> {
            expected.write(("James" + i).getBytes());
        });

        byte[] data = expected.commit();
        ReadPage page = ReadPage.create(data);
        byte[] buffer = new byte[100];

        assertAll(
                () -> assertEquals("James0", new String(buffer, 0, page.record(0, buffer))),
                () -> assertEquals("James5", new String(buffer, 0, page.record(5, buffer))),
                () -> assertEquals("James100", new String(buffer, 0, page.record(100, buffer))),
                () -> assertEquals("James999", new String(buffer, 0, page.record(999, buffer)))
        );

    }

}
