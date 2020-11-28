package query.page;

import query.page.read.ReadPage;
import query.page.read.ReadableSlottedPage;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;

public class DiskPageIndex implements PageIndex {

    private final int pageSize;
    private final File indexFileLocation;
    private final File dataFileLocation;
    private final RandomAccessFile index;
    private final RandomAccessFile data;

    private final byte[] pageBuffer;
    private final TreeMap<Integer, PageRecord> pages = new TreeMap<>();

    private boolean indexPageDirty = true;
    private int noOfPage = 0;

    public DiskPageIndex(int pageSize, Path indexFile, boolean isNew) {

        try {
            this.indexFileLocation = indexFile.toFile();
            this.dataFileLocation = new File(indexFileLocation + ".data");

            System.out.println("Index File " + indexFileLocation);
            System.out.println("Data File " + dataFileLocation);

            this.index = new RandomAccessFile(indexFileLocation, "rw");
            this.data = new RandomAccessFile(dataFileLocation, "rw");

            if (isNew) {
                this.pageSize = pageSize;
                writeDataHeader(pageSize);
                writeIndexHeader(pageSize);
            } else {

                this.index.seek(0);
                this.index.readByte();
                this.pageSize = this.index.readInt();
                this.noOfPage = this.index.readInt();

                readPagesInfo();
                PageRecord r = pages.lastEntry().getValue();
                this.data.seek(r.pageOffSet + r.pageSize);
                long pos = noOfPage * (4 + 8) + 1;
                this.data.seek(pos);
            }

            this.pageBuffer = new byte[this.pageSize];
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    public void writeIndexHeader(int pageSize) throws IOException {
        this.index.writeByte((byte) 1);
        this.index.writeInt(pageSize); // PageSize
        this.index.writeInt(noOfPage);
        this.index.getFD().sync();
    }

    public void writeDataHeader(int pageSize) throws IOException {
        this.data.writeByte((byte) 1); // Version
        this.data.writeInt(pageSize); // PageSize
        this.data.writeInt(noOfPage); // No Of pages
        this.data.getFD().sync();
    }

    @Override
    public void insert(int pageNumber, byte[] rawBytes) {
        noOfPage++;
        try {
            long recordOffset = writeData(rawBytes);
            writeIndex(pageNumber, recordOffset);
            commitRecord();
            commitIndex();
            flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void writeIndex(int pageNumber, long offSet) throws IOException {
        this.index.writeInt(pageNumber);
        this.index.writeLong(offSet);
    }

    public long writeData(byte[] rawBytes) throws IOException {
        long offSet = this.data.getFilePointer();
        this.data.write(rawBytes);
        return offSet;
    }

    public void commitRecord() throws IOException {
        long dataWritePosition = this.data.getFilePointer();
        this.data.seek(1 + 4);
        this.data.writeInt(noOfPage);
        this.data.seek(dataWritePosition);
    }

    public void commitIndex() throws IOException {
        long indexWritePosition = this.index.getFilePointer();
        this.index.seek(1 + 4);
        this.index.writeInt(noOfPage);
        this.index.seek(indexWritePosition);
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
        PageRecord page = pages.get(pageNo);
        try {
            long start = this.data.getFilePointer();
            this.data.seek(page.pageOffSet);
            this.data.read(pageBuffer, 0, page.pageSize);
            this.data.seek(start);

            ReadPage pageData = new ReadableSlottedPage(pageBuffer);
            return pageData.record(record, writeBuffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Collection<PageRecord> pages() {
        readPagesInfo();
        return pages.values();
    }

    public void readPagesInfo() {
        if (isNotDirty()) {
            return;
        }
        try {
            pages.clear();
            long startPositionPos = this.index.getFilePointer();
            this.index.seek(0);

            byte version = this.index.readByte();
            int pageSize = this.index.readInt();
            int pageCount = this.index.readInt();

            for (int page = 0; page < pageCount; page++) {
                int pageNo = index.readInt();
                long pageOffSet = index.readLong();
                pages.put(pageNo, new PageRecord(pageNo, pageSize, pageOffSet));
            }
            this.index.seek(startPositionPos);
            indexPageDirty = false;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    static class PageRecord {
        public final int pageId;
        public final int pageSize;
        public final long pageOffSet;

        public PageRecord(int pageId, int pageSize, long pageOffSet) {
            this.pageId = pageId;
            this.pageSize = pageSize;
            this.pageOffSet = pageOffSet;
        }

        @Override
        public String toString() {
            return String.format("PageRecord[Page Id %s ; Size %s ; From %s; To %s]", pageId, pageSize, pageOffSet, pageOffSet + pageSize);
        }
    }

    public static PageIndex create(int pageSize, Path indexFile) {
        return new DiskPageIndex(pageSize, indexFile, true);
    }

    public static PageIndex load(Path indexFile) {

        return new DiskPageIndex(0, indexFile, false);
    }

}
