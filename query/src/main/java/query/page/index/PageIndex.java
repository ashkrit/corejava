package query.page.index;

import java.util.Collection;
import java.util.Collections;

/**
 * Manages list of pages.
 * index is maintained in .index file and data is maintained in .data file
 */
public interface PageIndex {
    void insert(int pageNumber, byte[] data);

    int at(int pageNo, int record, byte[] writeBuffer);

    default Collection<PageRecord> pages() {
        return Collections.emptyList();
    }

    int noOfPages();
}
