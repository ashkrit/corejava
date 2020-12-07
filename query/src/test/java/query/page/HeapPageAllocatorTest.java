package query.page;

import org.junit.jupiter.api.Test;
import query.page.allocator.HeapPageAllocator;
import query.page.allocator.PageAllocator;
import query.page.read.ReadPage;
import query.page.write.WritePage;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static query.page.ApplicationClock.fromTs;
import static query.page.ApplicationClock.now;

public class HeapPageAllocatorTest {

    @Test
    public void create_heap_pages() {
        PageAllocator pa = new HeapPageAllocator((byte) 1, 1024);
        LocalDateTime now = fromTs(now());

        ApplicationClock.provider = () -> System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(10); // Move clock

        WritePage page = pa.newPage();

        assertEquals(1, pa.noOfPages());
        assertEquals(1024, pa.pageSize());
        assertEquals(1, pa.version());

        assertAll(
                () -> assertEquals(1, page.pageNumber()),
                () -> assertEquals(1007, page.capacity()),
                () -> assertEquals(0, page.noOfTuple()),
                () -> assertTrue(page.createdTime().isAfter(now))
        );
    }

    @Test
    public void read_heap_pages() {

        PageAllocator pa = new HeapPageAllocator((byte) 1, 1024);
        WritePage page = pa.newPage();

        page.write("Hello".getBytes());
        page.write("World".getBytes());
        pa.commit(page);

        ReadPage p = pa.readPage(page.pageNumber());

        byte[] buffer = new byte[1024];
        assertAll(
                () -> assertEquals("Hello", new String(buffer, 0, p.record(0, buffer))),
                () -> assertEquals("World", new String(buffer, 0, p.record(1, buffer)))
        );
    }


    @Test
    public void read_multiple_pages() {
        PageAllocator pa = new HeapPageAllocator((byte) 1, 1024);
        range(0, 10).forEach($ -> {
            WritePage page = pa.newPage();
            page.write(("Hello" + page.pageNumber()).getBytes());
            page.write(("World" + page.pageNumber()).getBytes());
            pa.commit(page);
        });

        byte[] buffer = new byte[1024];
        assertAll(
                () -> {
                    ReadPage p = pa.readPage(1);
                    assertEquals("Hello1", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World1", new String(buffer, 0, p.record(1, buffer)));
                },
                () -> {
                    ReadPage p = pa.readPage(10);
                    assertEquals("Hello10", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World10", new String(buffer, 0, p.record(1, buffer)));
                }
        );
    }

}
