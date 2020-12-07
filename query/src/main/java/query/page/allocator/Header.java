package query.page.allocator;

import java.nio.ByteBuffer;

public class Header {
    public byte version;
    public int pageSize;
    public int currentPageNo;
    public static int SIZE = 1 + 4 + 4; // Make sure to update this when field is added/removed

    public byte[] dataBuffer = new byte[SIZE];

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.wrap(dataBuffer);
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

    public byte[] allocatePageBuffer() {
        return new byte[pageSize];
    }

    public void checkPageNumber(int pageId) {
        if (pageId < 0) {
            throw new IllegalArgumentException("Page no is not positive - " + pageId);
        }
        if (pageId > currentPageNo) {
            throw new IllegalArgumentException(String.format("Invalid page %s , max page is %s", pageId, currentPageNo));
        }
    }

    public void checkPageOffset(long offSet) {

        if (offSet < 0) {
            throw new IllegalArgumentException("Offset  is not positive - " + offSet);
        }

        long pageOffset = offSet - SIZE;
        long misalignedBytes = pageOffset % pageSize;
        if (misalignedBytes != 0) {
            throw new IllegalArgumentException(String.format("Page Offset is invalid by %s", misalignedBytes));
        }

        long pageNo = pageOffset / pageSize;
        if (pageNo > currentPageNo - 1) {
            throw new IllegalArgumentException(String.format("Page Offset is overflowing - accessed page ", pageNo));
        }
    }
}
