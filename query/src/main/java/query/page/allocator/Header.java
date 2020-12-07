package query.page.allocator;

import java.nio.ByteBuffer;

public class Header {
    public byte version;
    public int pageSize;
    public int currentPageNo;
    public static int SIZE = 1 + 4 + 4;

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(SIZE);
        buffer.put(version);
        buffer.putInt(pageSize);
        buffer.putInt(currentPageNo);
        return buffer.array();
    }

    public Header fromBytes(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.version = buffer.get();
        this.pageSize = buffer.getInt();
        this.currentPageNo = buffer.getInt();
        return this;
    }

    public int nextPage() {
        return ++currentPageNo;
    }

    public long pageOffSet(int pageId) {
        long pageOffset = (pageId - 1) * 1L * pageSize;
        return Header.SIZE + pageOffset;
    }
}
