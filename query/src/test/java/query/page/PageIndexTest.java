package query.page;

import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

public class PageIndexTest {

    @Test
    public void build_page_index() {

        PageIndex index = new PageIndex();
        index.insert(1, 0, Paths.get("somefile"));
    }
}
