package query.timeseries.sst.disk;

import model.avro.page.SSTablePage;
import org.jetbrains.annotations.NotNull;
import query.page.allocator.DiskPageAllocator;
import query.page.allocator.PageAllocator;
import query.page.write.WritePage;
import query.timeseries.sst.PageRecord;
import query.timeseries.sst.SortedStringTable;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.NavigableMap;
import java.util.function.Function;

public class DiskSSTable<V> implements SortedStringTable<V> {

    private final SortedStringTable<V> underlyingStore;
    private final File storeLocation;
    private final String storeName;
    private final PageAllocator dataBlock;
    private final PageAllocator indexBlock;
    private final Function<V, byte[]> toBytes;

    private WritePage indexPage;
    private WritePage dataPage;

    public DiskSSTable(SortedStringTable<V> underlyingStore, File storeLocation, String storeName, Function<V, byte[]> toBytes) {
        this.underlyingStore = underlyingStore;
        this.storeLocation = storeLocation;
        this.storeName = storeName;
        this.dataBlock = new DiskPageAllocator((byte) 1, 1024, new File(storeLocation, storeName + ".1.data").toPath());
        this.indexBlock = new DiskPageAllocator((byte) 1, 1024, new File(storeLocation, storeName + ".1.index").toPath());
        this.toBytes = toBytes;

    }

    @Override
    public void append(String key, V value) {
        underlyingStore.append(key, value);
    }

    @Override
    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        underlyingStore.iterate(from, to, consumer);
    }

    @Override
    public Collection<PageRecord<V>> buffers() {
        return underlyingStore.buffers();
    }

    @Override
    public void update(int pageId, PageRecord<V> page) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void remove(int pageId) {
        throw new IllegalArgumentException("Not supported");
    }

    @Override
    public void flush() {
        List<PageRecord<V>> pages = new ArrayList<>(buffers());
        List<SSTablePage> pageList = writeDataBlock(pages);
        writeIndexBlock(pageList);

        pages.forEach(page -> underlyingStore.remove(page.getPageInfo().getPageId()));

    }

    private void writeIndexBlock(List<SSTablePage> pageList) {
        if (this.indexPage == null) {
            this.indexPage = this.indexBlock.newPage();
        }
        for (SSTablePage page : pageList) {
            byte[] pageBytes = toPageRecord(page);
            if (this.indexPage.write(pageBytes) == -1) {
                indexBlock.commit(indexPage);
                this.indexPage = this.indexBlock.newPage();
            }
        }
        indexBlock.commit(indexPage);
    }

    @NotNull
    private List<SSTablePage> writeDataBlock(List<PageRecord<V>> buffers) {
        List<SSTablePage> pageList = new ArrayList<>();

        if (this.dataPage == null) {
            this.dataPage = this.dataBlock.newPage();
        }

        SSTablePage pageInfo = null;

        for (PageRecord<V> buffer : buffers) {
            pageInfo = buffer.getPageInfo();
            NavigableMap<String, V> pageData = buffer.getPageData();

            for (V row : pageData.values()) {
                byte[] recordBytes = toBytes.apply(row);
                if (dataPage.write(recordBytes) == -1) {
                    commitPageAndAllocateNew(pageList, pageInfo);
                }
            }
        }
        long pageOffset = dataBlock.commit(dataPage);
        recordIndexPage(pageList, dataPage, pageInfo, pageOffset);
        return pageList;
    }

    private void commitPageAndAllocateNew(List<SSTablePage> pageList, SSTablePage pageInfo) {
        long pageOffset = dataBlock.commit(dataPage);
        recordIndexPage(pageList, dataPage, pageInfo, pageOffset);
        this.dataPage = dataBlock.newPage();
    }

    private void recordIndexPage(List<SSTablePage> pageList, WritePage dataPage, SSTablePage pageInfo, long pageOffset) {
        SSTablePage indexPage = SSTablePage
                .newBuilder(pageInfo)
                .setPageId(dataPage.pageNumber())
                .setOffSet(pageOffset)
                .build();
        pageList.add(indexPage);
    }

    private byte[] toPageRecord(SSTablePage pageInfo) {
        try {
            return pageInfo.toByteBuffer().array();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public String toString() {
        return String.format("%s [\n Index=%s \n Data=%s \n]", this.getClass().getSimpleName(), indexBlock.dataLocation(), dataBlock.dataLocation());
    }
}
