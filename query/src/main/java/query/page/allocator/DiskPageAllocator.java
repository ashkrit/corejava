package query.page.allocator;

import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import static query.page.ApplicationClock.now;

public class DiskPageAllocator implements PageAllocator {

    private final Path dataLocation;

    //Header
    private final Header header = new Header();
    private RandomAccessFile raf;

    public DiskPageAllocator(byte version, int pageSize, Path dataLocation) {
        header.version = version;
        header.pageSize = pageSize;
        this.dataLocation = dataLocation;
        if (dataLocation.toFile().exists()) {
            this.raf = SafeIO.open(dataLocation);
            byte[] headerData = new byte[Header.SIZE];
            SafeIO.read(raf, 0, headerData);
            header.fromBytes(headerData);
        } else {
            SafeIO.createNewFile(dataLocation);
            this.raf = SafeIO.open(dataLocation);
            SafeIO.write(raf, header.toBytes());
        }
    }

    @Override
    public WritePage newPage() {
        WritePage page = newPage(nextPage(), now());
        SafeIO.write(raf, 0, header.toBytes());
        return page;
    }

    @Override
    public void commit(WritePage page) {
        long writePosition = header.pageOffSet(page.pageNumber());
        SafeIO.write(raf, writePosition, page.commit());
        SafeIO.commit(raf);
    }

    @Override
    public ReadPage readPage(int pageId) {
        long readPosition = header.pageOffSet(pageId);
        byte[] pageBuffer = new byte[header.pageSize];
        SafeIO.read(raf, readPosition, pageBuffer);
        return new ReadableSlottedPage(pageBuffer);
    }

    @Override
    public int noOfPages() {
        return header.currentPageNo;
    }

    @Override
    public int pageSize() {
        return header.pageSize;
    }

    @Override
    public byte version() {
        return header.version;
    }

    private WritableSlotPage newPage(int page, long createdTs) {
        return new WritableSlotPage(header.pageSize, header.version, page, createdTs);
    }

    private int nextPage() {
        return header.nextPage();
    }

    static class Header {
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


}
