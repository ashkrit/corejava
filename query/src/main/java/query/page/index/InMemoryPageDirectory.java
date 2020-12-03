package query.page.index;

import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;

import java.util.Map;
import java.util.TreeMap;

public class InMemoryPageDirectory implements PageDirectory {

    private final Map<Integer, byte[]> pageIndex = new TreeMap<>();
    private final Map<Integer, ReadPage> loadedPage = new TreeMap<>();
    private int pageCount = 0;

    @Override
    public void insert(int pageNumber, byte[] data) {
        pageIndex.put(pageNumber, data);
        ++pageCount;
    }

    @Override
    public int at(int pageNo, int record, byte[] writeBuffer) {
        ReadPage page = readPage(pageNo);
        return page.record(record, writeBuffer);
    }

    @Override
    public int noOfPages() {
        return pageCount;
    }

    public ReadPage readPage(int pageNo) {
        ReadPage page = loadedPage.get(pageNo);
        if (page == null) {
            byte[] rawData = pageIndex.get(pageNo);
            loadedPage.put(pageNo, new ReadableSlottedPage(rawData));
        }
        page = loadedPage.get(pageNo);
        return page;
    }
}
