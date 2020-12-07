package query.page.allocator;

public class PageInfo {
    public final int pageId;
    public final long pageOff;

    public PageInfo(int pageId, long pageOff) {
        this.pageId = pageId;
        this.pageOff = pageOff;
    }
}
