package query.timeseries.sst.disk;

import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

public class OrderedPageDirectoryImpl implements OrderedPageDirectory {


    private final File indexFileLocation;
    private final File dataFileLocation;
    private final RandomAccessFile index;
    private final RandomAccessFile data;
    private final byte[] pageBuffer;
    private final TreeMap<Integer, PageInfoV1> pages = new TreeMap<>();
    private final int recordSize;

    private boolean indexPageDirty = true;
    private int noOfPage = 0;

    private static final int MAX_PAGES = 100;

    private PHeaderV1 pageHeader;

    private Map<Integer, ReadPage> recentPages = new LinkedHashMap<Integer, ReadPage>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ReadPage> eldest) {
            return size() > MAX_PAGES;
        }
    };

    public OrderedPageDirectoryImpl(int pageSize, Path indexFile, boolean isNew, int recordSize) {

        try {
            this.indexFileLocation = new File(indexFile.toFile().getAbsolutePath() + ".index");
            this.dataFileLocation = new File(indexFile.toFile().getAbsolutePath() + ".data");

            System.out.println("Index File " + indexFileLocation);
            System.out.println("Data File " + dataFileLocation);

            this.recordSize = recordSize;
            this.index = new RandomAccessFile(indexFileLocation, "rw");
            this.data = new RandomAccessFile(dataFileLocation, "rw");

            this.pageHeader = new PHeaderV1();
            this.pageHeader.version = (byte) 1;
            this.pageHeader.pageSize = pageSize;
            this.pageHeader.recordSize = PageInfoV1.MESSAGE_SIZE;
            this.pageHeader.noOfPages = 0;
            if (isNew) {
                writeIndexHeader(pageHeader);
                writeDataHeader(pageHeader);
            } else {
                readDirPageHeader();
                seekToWriteLocation();
            }
            this.pageBuffer = new byte[pageHeader.pageSize];
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void readDirPageHeader() throws IOException {
        this.index.seek(0);
        byte header[] = new byte[PHeaderV1.MESSAGE_SIZE];
        this.index.read(header);
        this.pageHeader = pageHeader.fromBytes(header);
    }

    public void seekToWriteLocation() throws IOException {
        readPagesInfo();
        PageInfoV1 r = pages.lastEntry().getValue();
        this.data.seek(r.offSet + pageHeader.pageSize);
    }


    public void writeIndexHeader(PHeaderV1 pageHeader) throws IOException {
        this.index.seek(0);
        this.index.write(pageHeader.toBytes());
        this.index.getFD().sync();
    }

    public void writeDataHeader(PHeaderV1 pageSize) throws IOException {
        this.data.writeByte((byte) 1); // Version
        this.data.writeInt(pageSize.pageSize); // PageSize
        this.data.writeInt(pageSize.noOfPages); // No Of pages
        this.data.getFD().sync();
    }

    @Override
    public void insert(int page, String min, String max, byte[] rawBytes) {
        pageHeader.nextPage();
        try {
            long recordOffset = writeData(rawBytes);
            writeIndex(page, min, max, recordOffset);
            commitData();
            commitIndex();
            flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writeIndex(int page, String min, String max, long offSet) {
        try {
            PageInfoV1 pageInfo = new PageInfoV1();
            pageInfo.pageId = page;
            pageInfo.minValue = Integer.parseInt(min);
            pageInfo.maxValue = Integer.parseInt(max);
            pageInfo.offSet = offSet;
            this.index.write(pageInfo.toBytes());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public long writeData(byte[] rawBytes) throws IOException {
        long offSet = this.data.getFilePointer();
        this.data.write(rawBytes);
        return offSet;
    }

    public void commitData() throws IOException {
        long writeMark = this.data.getFilePointer();
        this.data.seek(1 + 4);
        this.data.writeInt(pageHeader.noOfPages);
        this.data.seek(writeMark);
    }

    public void commitIndex() throws IOException {
        long writeMark = this.index.getFilePointer();
        writeIndexHeader(pageHeader);
        this.index.seek(writeMark);
    }

    public void flush() throws IOException {
        this.data.getFD().sync();
        this.index.getFD().sync();
        markIndexDirty();
    }

    private void markIndexDirty() {
        this.indexPageDirty = true;
    }

    private boolean isNotDirty() {
        return !indexPageDirty;
    }

    @Override
    public int at(int pageNo, int record, byte[] writeBuffer) {
        readPagesInfo();
        ReadPage page = recentPages.computeIfAbsent(pageNo, this::readPage);
        return page.record(record, writeBuffer);
    }

    private ReadPage readPage(int pageNo) {

        try {
            PageInfoV1 pageRecord = pages.get(pageNo);
            long start = this.data.getFilePointer();

            this.data.seek(pageRecord.offSet);
            this.data.read(pageBuffer, 0, pageHeader.pageSize);

            this.data.seek(start);

            return new ReadableSlottedPage(pageBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Collection<PageInfoV1> pages() {
        readPagesInfo();
        return pages.values();
    }

    @Override
    public int noOfPages() {
        return noOfPage;
    }

    public void readPagesInfo() {
        if (isNotDirty()) {
            return;
        }
        try {
            pages.clear();
            long startPositionPos = this.index.getFilePointer();
            readDirPageHeader();
            byte[] pageRecord = new byte[PageInfoV1.MESSAGE_SIZE];
            for (int page = 0; page < pageHeader.noOfPages; page++) {
                index.read(pageRecord);
                PageInfoV1 info = new PageInfoV1();
                info.fromBytes(pageRecord);
                pages.put(info.pageId, info);
            }
            this.index.seek(startPositionPos);
            indexPageDirty = false;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static OrderedPageDirectory create(int pageSize, Path baseFile) {
        return new OrderedPageDirectoryImpl(pageSize, baseFile, true, 0);
    }

    public static OrderedPageDirectory load(Path indexFile) {
        return new OrderedPageDirectoryImpl(0, indexFile, false, 0);
    }
}
