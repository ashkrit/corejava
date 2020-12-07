package query.page.allocator;

import query.page.ApplicationClock;
import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;
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
        return newPage(nextPage(), now());
    }

    private WritableSlotPage newPage(int page, long createdTs) {
        return new WritableSlotPage(pageSize, version, page, createdTs);
    }

    private int nextPage() {
        return ++currentPageNo;
    }

    @Override
    public long commit(WritePage page) {
        pages.put(page.pageNumber(), page);
        return 0;
    }

    @Override
    public ReadPage readByPageId(int pageId) {
        return new ReadableSlottedPage(pages.get(pageId).commit());
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
