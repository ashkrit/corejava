package query.page;

import org.junit.jupiter.api.Test;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PageIndexTest {

    @Test
    public void access_record_page_and_index() {

        PageIndex index = new PageIndex();

        List<String> novels = asList("Casino Royale",
                "Live and Let Die",
                "Moonraker",
                "Diamonds Are Forever",
                "From Russia, with Love",
                "Dr. No",
                "Goldfinge");


        int pageNo = 0;
        WritePage page = new WritableSlotPage(1024 * 16, (byte) 0, pageNo++, System.currentTimeMillis());
        novels.forEach(r -> page.write(r.getBytes()));
        byte[] data = page.commit();
        System.out.println(page);

        index.insert(page.pageNumber(), data);

        byte[] buffer = new byte[100];

        assertEquals("From Russia, with Love", new String(buffer, 0, index.valueAt(0, 4, buffer)));

    }
}
