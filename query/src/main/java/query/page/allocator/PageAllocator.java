package query.page.allocator;

import query.page.read.ReadPage;
import query.page.write.WritePage;

public interface PageAllocator {
    WritePage newPage();

    void commit(WritePage page);

    ReadPage readPage(int pageId);

    int noOfPages();

    int pageSize();

    byte version();
}
