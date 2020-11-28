package query.page;

import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;

import java.util.Map;
import java.util.TreeMap;

public class InMemoryPageIndex implements PageIndex {

    private final Map<Integer, byte[]> pageIndex = new TreeMap<>();
    private final Map<Integer, ReadPage> loadedPage = new TreeMap<>();

    @Override
    public void insert(int pageNumber, byte[] data) {
        pageIndex.put(pageNumber, data);
    }

    @Override
    public int at(int pageNo, int record, byte[] writeBuffer) {
        ReadPage page = readPage(pageNo);
        return page.record(record, writeBuffer);
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
