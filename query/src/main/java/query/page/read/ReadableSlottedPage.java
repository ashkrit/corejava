package query.page.read;

import query.page.PageOffSets;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

public class ReadableSlottedPage implements ReadPage {

    public static final int POINTER_SIZE = 4;
    private final byte[] readData;
    private final ByteBuffer readBuffer;

    private byte version;
    private int pageNumber;
    private long createdTs;
    private int totalTuple;

    public ReadableSlottedPage(byte[] readData) {
        this.readData = readData;
        this.readBuffer = ByteBuffer.wrap(readData)
                .asReadOnlyBuffer();
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
    public int record(int index, byte[] writeBuffer) {
        return read(writeBuffer, index, readBuffer);
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

    private int read(byte[] writeBuffer, int recordIndex, ByteBuffer readBuffer) {
        int slotIndex = slotIndex(recordIndex);
        int startPosition = startPosition(recordIndex, slotIndex, readBuffer);
        int recordSize = recordSize(readBuffer, slotIndex, startPosition); // Bytes to read from current position
        return readTuple(writeBuffer, startPosition, recordSize, readBuffer);
    }

    private int recordSize(ByteBuffer readBuffer, int slotIndex, int startPosition) {
        return readBuffer.getInt(slotIndex) - startPosition;
    }

    private int slotIndex(int record) {
        return (readData.length - (record * POINTER_SIZE)) - POINTER_SIZE;
    }
}
