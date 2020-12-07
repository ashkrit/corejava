package query.page.allocator;

import query.page.read.ReadPage;
import query.page.write.WritePage;

import java.util.List;

public interface PageAllocator {
    WritePage newPage();

    long commit(WritePage page);

    ReadPage readByPageId(int pageId);

    int noOfPages();

    int pageSize();

    byte version();

    default List<PageInfo> pages() {
        throw new IllegalArgumentException("Not applicable");
    }

    default ReadPage readByPageOffset(long offSet) {
        throw new IllegalArgumentException("Not applicable");
    }

}
