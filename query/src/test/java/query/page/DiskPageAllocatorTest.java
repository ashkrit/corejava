package query.page;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import query.page.allocator.*;
import query.page.read.ReadPage;
import query.page.write.WritePage;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.IntStream.range;
import static org.junit.jupiter.api.Assertions.*;
import static query.page.ApplicationClock.fromTs;
import static query.page.ApplicationClock.now;

public class DiskPageAllocatorTest {

    @Test
    public void create_disk_pages() {

        Path dataFile = dataFilePath("disk.1.data." + System.nanoTime());

        PageAllocator pa = new DiskPageAllocator((byte) 1, 1024, dataFile);
        LocalDateTime now = fromTs(now());

        ApplicationClock.provider = () -> System.currentTimeMillis() + 1000; // Move clock

        WritePage page = pa.newPage();

        assertEquals(1, pa.noOfPages());
        assertEquals(1024, pa.pageSize());
        assertEquals(1, pa.version());

        assertAll(
                () -> assertEquals(1, page.pageNumber()),
                () -> assertEquals(1007, page.capacity()),
                () -> assertEquals(0, page.noOfTuple()),
                () -> assertTrue(page.createdTime().isAfter(now)),
                () -> assertTrue(dataFile.toFile().exists())
        );
    }

    @NotNull
    private Path dataFilePath(String fileName) {
        File f = new File(System.getProperty("java.io.tmpdir"), "datastore");
        f.mkdirs();
        Path dataFile = Paths.get(f.getAbsolutePath(), fileName);
        return dataFile;
    }

    @Test
    public void read_single_disk_pages() {

        Path dataFile = dataFilePath("disk.1.data." + System.nanoTime());
        PageAllocator pa = new DiskPageAllocator((byte) 1, 1024, dataFile);

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
    public void read_multiple_disk_pages() {
        Path dataFile = dataFilePath("disk.1.data." + System.nanoTime());
        PageAllocator pa = new DiskPageAllocator((byte) 1, 1024, dataFile);

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

    @Test
    public void read_pages_from_saved_file() {

        Path dataFile = dataFilePath("disk.1.data." + System.nanoTime());
        PageAllocator pa = new DiskPageAllocator((byte) 1, 1024, dataFile);

        range(0, 10).forEach($ -> {
            WritePage page = pa.newPage();
            page.write(("Hello" + page.pageNumber()).getBytes());
            page.write(("World" + page.pageNumber()).getBytes());
            pa.commit(page);
        });


        PageAllocator anotherPage = new DiskPageAllocator((byte) 1, 1024, dataFile);
        byte[] buffer = new byte[1024];
        assertAll(
                () -> {
                    ReadPage p = anotherPage.readPage(1);
                    assertEquals("Hello1", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World1", new String(buffer, 0, p.record(1, buffer)));
                },
                () -> {
                    ReadPage p = anotherPage.readPage(10);
                    assertEquals("Hello10", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World10", new String(buffer, 0, p.record(1, buffer)));
                }
        );
    }


    @Test
    public void load_page_metadata() {

        Path dataFile = dataFilePath("disk.1.data." + System.nanoTime());
        PageAllocator pa = new DiskPageAllocator((byte) 1, 1024, dataFile);

        range(0, 10).forEach($ -> {
            WritePage page = pa.newPage();
            page.write(("Hello" + page.pageNumber()).getBytes());
            page.write(("World" + page.pageNumber()).getBytes());
            pa.commit(page);
        });

        List<PageInfo> pages = pa.pages();
        byte[] buffer = new byte[1024];
        assertAll(
                () -> {
                    PageInfo pageInfo = pages.get(0);
                    ReadPage p = pa.readPage(pageInfo.pageOff);
                    assertEquals("Hello1", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World1", new String(buffer, 0, p.record(1, buffer)));
                },
                () -> {
                    PageInfo pageInfo = pages.get(9);
                    ReadPage p = pa.readPage(pageInfo.pageOff);
                    assertEquals("Hello10", new String(buffer, 0, p.record(0, buffer)));
                    assertEquals("World10", new String(buffer, 0, p.record(1, buffer)));
                }
        );
    }

}
