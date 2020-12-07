package query.page.allocator;

import query.page.io.BlockRandomAccessFile;
import query.page.io.SafeIO;
import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.nio.file.Path;

import static query.page.ApplicationClock.now;

public class DiskPageAllocator implements PageAllocator {

    private final Header header = new Header();
    private final BlockRandomAccessFile rafBlock;

    public DiskPageAllocator(byte version, int pageSize, Path dataLocation) {
        header.version = version;
        header.pageSize = pageSize;
        if (dataLocation.toFile().exists()) {
            this.rafBlock = new BlockRandomAccessFile(SafeIO.open(dataLocation));
            byte[] headerData = new byte[Header.SIZE];
            rafBlock.read(0, headerData);
            header.fromBytes(headerData);
        } else {
            SafeIO.createNewFile(dataLocation);
            this.rafBlock = new BlockRandomAccessFile(SafeIO.open(dataLocation));
            rafBlock.write(0, header.toBytes());
        }
    }

    @Override
    public WritePage newPage() {
        WritePage page = newPage(nextPage(), now());
        rafBlock.write(0, header.toBytes());
        return page;
    }

    @Override
    public void commit(WritePage page) {
        long writePosition = header.pageOffSet(page.pageNumber());
        rafBlock.write(writePosition, page.commit());
        rafBlock.commit();
    }

    @Override
    public ReadPage readPage(int pageId) {
        long readPosition = header.pageOffSet(pageId);
        byte[] pageBuffer = new byte[header.pageSize];
        rafBlock.read(readPosition, pageBuffer);
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

}
