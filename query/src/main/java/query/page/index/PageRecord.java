package query.page.index;

public class PageRecord {
    public final int pageId;
    public final int pageSize;
    public final long pageOffSet;

    public PageRecord(int pageId, int pageSize, long pageOffSet) {
        this.pageId = pageId;
        this.pageSize = pageSize;
        this.pageOffSet = pageOffSet;
    }

    @Override
    public String toString() {
        return String.format("PageRecord[Page Id %s ; Size %s ; From %s; To %s]", pageId, pageSize, pageOffSet, pageOffSet + pageSize);
    }
}
