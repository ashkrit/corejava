package query.page.read;

import query.page.PageOffSets;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

public class ReadableSlottedPage implements ReadPage {

    public static final int POINTER_SIZE = 4;
    private final byte[] data;
    private final ByteBuffer buffer;

    private byte version;
    private int pageNumber;
    private long createdTs;
    private int totalTuple;

    public ReadableSlottedPage(byte[] data) {
        this.data = data;
        this.buffer = ByteBuffer.wrap(data)
                .asReadOnlyBuffer();
        readHeaders();
    }

    private void readHeaders() {
        version = buffer.get(PageOffSets.PAGE_VERSION);
        pageNumber = buffer.getInt(PageOffSets.PAGE_NUMBER);
        createdTs = buffer.getLong(PageOffSets.CREATED_TS);
        totalTuple = buffer.getInt(PageOffSets.NO_OF_TUPLE);
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
            final ByteBuffer readBuffer = buffer;

            @Override
            public int next(byte[] writeBuffer) {
                if (!hasNext()) {
                    return -1;
                }
                int bytesToRead = read(writeBuffer, this.current, readBuffer);
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
    public int record(int index, byte[] rawData) {
        int bytesToRead = read(rawData, index, buffer);
        return bytesToRead;
    }

    @Override
    public String toString() {
        return String.format("SlotPage (CreatedAt: %s;Page: %s;Version: %s;Tuple Count: %s)", createdTime(), pageNumber, version, totalTuple);
    }

    @Override
    public LocalDateTime createdTime() {
        return ofInstant(ofEpochMilli(createdTs), systemDefault());
    }

    private int startPosition(int record, int slotIndex, ByteBuffer readBuffer) {
        if (record == 0) {
            return PageOffSets.DATA_OFFSET;
        } else {
            return readBuffer.getInt(slotIndex + POINTER_SIZE);
        }
    }

    private int readTuple(byte[] writeBuffer, int startPos, int bytesToRead, ByteBuffer readBuffer) {
        for (int start = 0; start < bytesToRead; start++) {
            writeBuffer[start] = readBuffer.get(startPos + start);
        }
        return bytesToRead;
    }

    private int read(byte[] writeBuffer, int record, ByteBuffer readBuffer) {
        int slotIndex = (data.length - (record * POINTER_SIZE)) - POINTER_SIZE;
        int startPosition = startPosition(record, slotIndex, readBuffer);
        int bytesToRead = readBuffer.getInt(slotIndex) - startPosition; // Bytes to read from current position
        bytesToRead = readTuple(writeBuffer, startPosition, bytesToRead, readBuffer);
        return bytesToRead;
    }
}
