package query.page.read;

import query.page.PageOffSets;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

public class ReadableSlottedPage implements ReadPage {

    public static final int POINTER_SIZE = 4;
    private final ByteBuffer readBuffer;
    private final RecordReader recordReader;

    private byte version;
    private int pageNumber;
    private long createdTs;
    private int totalTuple;

    public ReadableSlottedPage(byte[] readData) {
        this.readBuffer = ByteBuffer.wrap(readData).asReadOnlyBuffer();
        this.recordReader = new RecordReader(POINTER_SIZE, readData.length);
        readHeaders();

    }

    private void readHeaders() {
        version = readBuffer.get(PageOffSets.PAGE_VERSION);
        pageNumber = readBuffer.getInt(PageOffSets.PAGE_NUMBER);
        createdTs = readBuffer.getLong(PageOffSets.CREATED_TS);
        totalTuple = readBuffer.getInt(PageOffSets.NO_OF_TUPLE);
    }

    @Override
    public short version() {
        return version;
    }

    @Override
    public int pageNumber() {
        return pageNumber;
    }

    @Override
    public int totalRecords() {
        return totalTuple;
    }

    @Override
    public PageIterator newIterator() {

        return new PageIterator() {
            int current = 0;
            final int total = totalTuple;
            final ByteBuffer readBuffer = ReadableSlottedPage.this.readBuffer;

            @Override
            public int next(byte[] writeBuffer) {
                if (!hasNext()) {
                    return -1;
                }
                int bytesToRead = recordReader.read(writeBuffer, this.current, readBuffer);
                this.current++; // Move to next slot
                return bytesToRead;
            }


            @Override
            public boolean hasNext() {
                return current < total;
            }
        };
    }

    @Override
    public int record(int index, byte[] writeBuffer) {
        return recordReader.read(writeBuffer, index, readBuffer);
    }

    @Override
    public String toString() {
        return String.format("SlotPage (CreatedAt: %s;Page: %s;Version: %s;Tuple Count: %s)", createdTime(), pageNumber, version, totalTuple);
    }

    @Override
    public LocalDateTime createdTime() {
        return ofInstant(ofEpochMilli(createdTs), systemDefault());
    }

}
