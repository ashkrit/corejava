package query.page;

import java.util.Collection;
import java.util.Collections;

public interface PageIndex {
    void insert(int pageNumber, byte[] data);

    int at(int pageNo, int record, byte[] writeBuffer);

    default Collection<DiskPageIndex.PageRecord> pages() {
        return Collections.emptyList();
    }
}
