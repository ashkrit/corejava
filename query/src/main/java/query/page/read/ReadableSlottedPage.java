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
            final ByteBuffer localBuffer = buffer;

            @Override
            public int next(byte[] buffer) {
                if (!hasNext()) {
                    return -1;
                }
                int slotIndex = (data.length - (current * POINTER_SIZE)) - POINTER_SIZE;
                int startPosition = startPosition(slotIndex);
                int bytesToRead = localBuffer.getInt(slotIndex) - startPosition; // Bytes to read from current position

                readTuple(buffer, startPosition, bytesToRead);

                current++; // Move to next slot
                return bytesToRead;
            }

            public void readTuple(byte[] buffer, int startPos, int bytesToRead) {
                for (int start = 0; start < bytesToRead; start++) {
                    buffer[start] = localBuffer.get(startPos + start);
                }
            }

            public int startPosition(int slotIndex) {
                if (current == 0) {
                    return PageOffSets.DATA_OFFSET;
                } else {
                    return localBuffer.getInt(slotIndex + POINTER_SIZE);
                }
            }

            @Override
            public boolean hasNext() {
                return current < total;
            }
        };
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
