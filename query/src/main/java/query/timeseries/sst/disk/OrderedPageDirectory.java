package query.timeseries.sst.disk;

import java.util.Collection;
import java.util.Collections;

/**
 * Manages list of pages.
 * index is maintained in .index file and data is maintained in .data file
 */
public interface OrderedPageDirectory {
    void insert(int page, String min, String max, byte[] data);

    int at(int pageNo, int record, byte[] writeBuffer);

    default Collection<PageInfoV1> pages() {
        return Collections.emptyList();
    }

    int noOfPages();
}
