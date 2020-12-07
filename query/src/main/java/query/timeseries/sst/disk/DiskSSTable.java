package query.timeseries.sst.disk;

import model.avro.page.SSTablePage;
import query.page.allocator.DiskPageAllocator;
import query.page.allocator.PageAllocator;
import query.page.read.ReadPage;
import query.page.write.WritePage;
import query.timeseries.sst.PageRecord;
import query.timeseries.sst.SortedStringTable;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.function.Function;

public class DiskSSTable<V> implements SortedStringTable<V> {

    public static final int BUFFER_FULL = -1;
    private final SortedStringTable<V> underlyingStore;
    private final File storeLocation;
    private final String storeName;
    private final PageAllocator dataBlock;
    private final PageAllocator indexBlock;
    private final Function<V, byte[]> toBytes;
    private final Function<ByteBuffer, V> fromBytes;
    private final Function<V, String> pk;

    private WritePage indexPage;
    private WritePage dataPage;
    private int recordsScanned = 0;

    public DiskSSTable(SortedStringTable<V> underlyingStore, File storeLocation, String storeName,
                       Function<V, byte[]> toBytes, Function<ByteBuffer, V> fromBytes, Function<V, String> pk) {
        this.underlyingStore = underlyingStore;
        this.storeLocation = storeLocation;
        this.storeName = storeName;
        this.dataBlock = new DiskPageAllocator((byte) 1, 1024, new File(storeLocation, storeName + ".1.data").toPath());
        this.indexBlock = new DiskPageAllocator((byte) 1, 1024, new File(storeLocation, storeName + ".1.index").toPath());
        this.toBytes = toBytes;
        this.fromBytes = fromBytes;
        this.pk = pk;
    }

    @Override
    public void append(String key, V value) {
        underlyingStore.append(key, value);
    }

    @Override
    public void iterate(String from, String to, Function<V, Boolean> consumer) {
        underlyingStore.iterate(from, to, consumer);

        recordsScanned = 0;
        int scannedPages = 0;
        Function<NavigableMap<String, V>, NavigableMap<String, V>> filter = predicate(from, to);

        //Iterate over disk
        int pageCount = this.indexBlock.noOfPages();
        byte[] buffer = new byte[1024];
        NavigableMap<String, V> pageData = new TreeMap<>();

        for (int indexPageCounter = 1; indexPageCounter <= pageCount; indexPageCounter++) {
            ReadPage indexPage = this.indexBlock.readByPageId(indexPageCounter);
            System.out.println("Page:" + indexPage);
            for (int indexPageRecordCounter = 0; indexPageRecordCounter < indexPage.totalRecords(); indexPageRecordCounter++) {

                int bytesRead = indexPage.record(indexPageRecordCounter, buffer);
                scannedPages++;
                SSTablePage pageIndex = readIndexRecord(buffer, bytesRead);
                loadPageData(buffer, pageData, this.dataBlock.readByPageId(pageIndex.getPageId()));

                if (!process(consumer, filter.apply(pageData))) {
                    return;
                }
            }
        }

        System.out.println("Disk Scan " + recordsScanned + " Scanned pages " + scannedPages);

    }

    private void loadPageData(byte[] buffer, NavigableMap<String, V> pageData, ReadPage dataPage) {
        pageData.clear();
        for (int rows = 0; rows < dataPage.totalRecords(); rows++) {
            int size = dataPage.record(rows, buffer);
            V recordToSearch = fromBytes.apply(ByteBuffer.wrap(buffer, 0, size));
            pageData.put(pk.apply(recordToSearch), recordToSearch);
        }
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> predicate(String from, String to) {
        if (from != null && to != null) {
            return bt(from, to);
        } else if (from != null) {
            return gt(from);
        } else if (to != null) {
            return lt(to);
        }
        throw new IllegalArgumentException("Not supported");
    }

    private SSTablePage readIndexRecord(byte[] buffer, int bytesRead) {
        try {
            return SSTablePage.fromByteBuffer(ByteBuffer.wrap(buffer, 0, bytesRead));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
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
        if (pages.isEmpty()) return;

        List<SSTablePage> pageList = writeDataBlock(pages);
        writeIndexBlock(pageList);
        pages.forEach(page -> underlyingStore.remove(page.getPageInfo().getPageId()));

    }

    private void writeIndexBlock(List<SSTablePage> pageList) {
        int indexPageCount = 0;
        int indexRecordCount = 0;
        if (this.indexPage == null) {
            this.indexPage = this.indexBlock.newPage();
        }
        for (SSTablePage page : pageList) {
            byte[] pageBytes = toPageRecord(page);
            indexRecordCount++;
            if (this.indexPage.write(pageBytes) == BUFFER_FULL) {
                indexBlock.commit(indexPage);
                indexPageCount++;
                this.indexPage = this.indexBlock.newPage();
                this.indexPage.write(pageBytes);
            }
        }
        indexBlock.commit(indexPage);
        indexPageCount++;

        System.out.println("Index Pages " + indexPageCount + " Index Record " + indexRecordCount + " Capacity " + indexPage.capacity());
    }

    private List<SSTablePage> writeDataBlock(List<PageRecord<V>> buffers) {
        int recordCount = 0;
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
                recordCount++;
                if (dataPage.write(recordBytes) == BUFFER_FULL) {
                    commitPageAndAllocateNew(pageList, pageInfo);
                    dataPage.write(recordBytes);
                }
            }
        }
        long pageOffset = dataBlock.commit(dataPage);
        recordIndexPage(pageList, dataPage, pageInfo, pageOffset);

        System.out.println("Records Written " + recordCount);

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


    private Function<NavigableMap<String, V>, NavigableMap<String, V>> bt(String from, String to) {
        return i -> i.subMap(from, true, to, true);
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> lt(String to) {
        return i -> i.headMap(to, true);
    }

    private Function<NavigableMap<String, V>, NavigableMap<String, V>> gt(String from) {
        return i -> i.tailMap(from, true);
    }

    private boolean process(Function<V, Boolean> fn, NavigableMap<String, V> matched) {
        for (Map.Entry<String, V> e : matched.entrySet()) {
            recordsScanned++;
            if (!fn.apply(e.getValue())) {
                return false;
            }
        }
        return true;
    }
}
