package query.page.allocator;

import query.page.ApplicationClock;
import query.page.read.ReadPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.util.HashMap;
import java.util.Map;

import static query.page.ApplicationClock.now;

public class HeapPageAllocator implements PageAllocator {
    private final int pageSize;
    private final byte version;
    private int currentPageNo;
    private Map<Integer, WritePage> pages = new HashMap<>();

    public HeapPageAllocator(byte version, int pageSize) {
        this.version = version;
        this.pageSize = pageSize;
    }

    @Override
    public WritePage newPage() {
        int page = nextPage();
        long createdTs = now();
        pages.put(page, newPage(page, createdTs));
        return pages.get(page);
    }

    private WritableSlotPage newPage(int page, long createdTs) {
        return new WritableSlotPage(pageSize, version, page, createdTs);
    }

    private int nextPage() {
        return ++currentPageNo;
    }

    @Override
    public void commit(WritePage page) {

    }

    @Override
    public ReadPage readPage(int pageId) {
        return null;
    }

    @Override
    public int noOfPages() {
        return currentPageNo;
    }

    @Override
    public int pageSize() {
        return pageSize;
    }

    @Override
    public byte version() {
        return version;
    }
}
