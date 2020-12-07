package query.page.allocator;

import query.page.io.BlockRandomAccessFile;
import query.page.io.SafeIO;
import query.page.read.ReadPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static query.page.ApplicationClock.now;

public class DiskPageAllocator implements PageAllocator {

    private final Header header = new Header();
    private final BlockRandomAccessFile rafBlock;

    public DiskPageAllocator(byte version, int pageSize, Path dataLocation) {
        header.version = version;
        header.pageSize = pageSize;
        if (dataLocation.toFile().exists()) {
            this.rafBlock = new BlockRandomAccessFile(SafeIO.open(dataLocation));
            readHeader();
        } else {
            SafeIO.createNewFile(dataLocation);
            this.rafBlock = new BlockRandomAccessFile(SafeIO.open(dataLocation));
            writeHeader();
        }
    }

    public void writeHeader() {
        rafBlock.write(0, header.toBytes());
    }

    private void readHeader() {
        rafBlock.read(0, header.dataBuffer);
        header.fromBytes(header.dataBuffer);
    }

    @Override
    public WritePage newPage() {
        WritePage page = newPage(nextPage(), now());
        writeHeader();
        return page;
    }

    @Override
    public void commit(WritePage page) {
        long writePosition = header.pageOffSet(page.pageNumber());
        rafBlock.write(writePosition, page.commit());
        rafBlock.commit();
    }

    @Override
    public ReadPage readByPageId(int pageId) {

        header.checkPageNumber(pageId);

        long readPosition = header.pageOffSet(pageId);
        byte[] pageBuffer = header.allocatePageBuffer();
        rafBlock.read(readPosition, pageBuffer);
        return ReadPage.create(pageBuffer);
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

    @Override
    public List<PageInfo> pages() {
        readHeader();
        return IntStream
                .range(0, header.currentPageNo)
                .mapToObj(index -> new PageInfo(index + 1, header.pageOffSet(index + 1)))
                .collect(Collectors.toList());
    }

    @Override
    public ReadPage readByPageOffset(long offSet) {

        header.checkPageOffset(offSet);
        byte[] pageBuffer = header.allocatePageBuffer();
        rafBlock.read(offSet, pageBuffer);
        return ReadPage.create(pageBuffer);
    }


    private WritePage newPage(int page, long createdTs) {
        return new WritableSlotPage(header.pageSize, header.version, page, createdTs);
    }

    private int nextPage() {
        return header.nextPage();
    }

}
