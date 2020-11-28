package query.page;

import org.junit.jupiter.api.Test;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageIndexTest {

    int KB_8 = 1024 * 8;

    @Test
    public void access_record_page_and_index() {


        InMemoryPageIndex index = new InMemoryPageIndex();

        List<String> novels = asList("Casino Royale",
                "Live and Let Die",
                "Moonraker",
                "Diamonds Are Forever",
                "From Russia, with Love",
                "Dr. No",
                "Goldfinge");


        int pageNo = 0;

        WritePage page = new WritableSlotPage(KB_8, (byte) 0, pageNo++, System.currentTimeMillis());
        novels.forEach(r -> page.write(r.getBytes()));
        byte[] data = page.commit();
        index.insert(page.pageNumber(), data);

        byte[] buffer = new byte[100];

        assertEquals("From Russia, with Love", new String(buffer, 0, index.at(0, 4, buffer)));

    }

    @Test
    public void save_page_index_to_disk() {

        File f = new File(System.getProperty("java.io.tmpdir"), "datastore");
        f.mkdirs();
        PageIndex index = DiskPageIndex.create(KB_8, new File(f, "index.1").toPath());

        IntStream.range(0, 10).forEach(pageIndex -> {
            List<String> records = IntStream
                    .range(0, 100).mapToObj(record -> String.format("record_(%s,%s)", pageIndex, record))
                    .collect(toList());
            insert(index, pageIndex, records);
        });

        byte[] buffer = new byte[100];

        assertEquals("record_(0,4)", new String(buffer, 0, index.at(0, 4, buffer)));
        assertEquals("record_(1,4)", new String(buffer, 0, index.at(1, 4, buffer)));
        assertEquals("record_(3,6)", new String(buffer, 0, index.at(3, 6, buffer)));

    }


    @Test
    public void read_loaded_page_index() {

        File f = new File(System.getProperty("java.io.tmpdir"), "datastore");
        f.mkdirs();
        Path indexFile = new File(f, "index.2").toPath();
        logRecords(indexFile);

        PageIndex index = DiskPageIndex.load(indexFile);

        byte[] buffer = new byte[100];

        assertEquals("record_(0,4)", new String(buffer, 0, index.at(0, 4, buffer)));
        assertEquals("record_(1,4)", new String(buffer, 0, index.at(1, 4, buffer)));
        assertEquals("record_(3,6)", new String(buffer, 0, index.at(3, 6, buffer)));


    }

    public void logRecords(Path indexFile) {
        PageIndex index = DiskPageIndex.create(KB_8, indexFile);
        IntStream.range(0, 10).forEach(pageIndex -> {
            List<String> records = IntStream
                    .range(0, 100).mapToObj(record -> String.format("record_(%s,%s)", pageIndex, record))
                    .collect(toList());
            insert(index, pageIndex, records);
        });
    }

    public void insert(PageIndex index, int pageNumber, List<String> records) {
        WritePage page = new WritableSlotPage(KB_8, (byte) 0, pageNumber, System.currentTimeMillis());
        records.forEach(r -> page.write(r.getBytes()));
        index.insert(page.pageNumber(), page.commit());
    }
}
