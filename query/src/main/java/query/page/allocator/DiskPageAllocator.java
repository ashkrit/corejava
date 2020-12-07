package query.page.allocator;

import query.page.read.ReadPage;
import query.page.write.WritableSlotPage;
import query.page.write.WritePage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static query.page.ApplicationClock.now;

public class DiskPageAllocator implements PageAllocator {

    private final Path dataLocation;

    private final byte version;
    private final int pageSize;
    private int currentPageNo;

    public DiskPageAllocator(byte version, int pageSize, Path dataLocation) {
        this.version = version;
        this.pageSize = pageSize;
        this.dataLocation = dataLocation;
        if (!dataLocation.toFile().exists()) {
            createNewFile(dataLocation);
        }
    }

    private void createNewFile(Path dataLocation) {
        try {
            dataLocation.toFile().createNewFile();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public WritePage newPage() {
        return newPage(nextPage(), now());
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

    private WritableSlotPage newPage(int page, long createdTs) {
        return new WritableSlotPage(pageSize, version, page, createdTs);
    }

    private int nextPage() {
        return ++currentPageNo;
    }
}
