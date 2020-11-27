package query.page.read;

import query.page.PageOffSets;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

public class ReadableSlottedPage implements ReadPage {

    private final byte[] data;
    private final ByteBuffer buffer;

    private byte version;
    private int pageNumber;
    private long createdTs;
    private int totalTuple;

    private int currentTuple;

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
    public int next(byte[] buffer) {
        if (!hasNext()) {
            return -1;
        }
        int slotIndex = (data.length - (currentTuple) * 4) - 4;
        int readIndex = 0;
        if (currentTuple == 0) {
            readIndex = PageOffSets.DATA_OFFSET;
        } else {
            readIndex = this.buffer.getInt(slotIndex + 4);
        }
        int bytesToRead = this.buffer.getInt(slotIndex) - readIndex; // Bytes to read from current position
        this.buffer.position(readIndex);
        this.buffer.get(buffer, 0, bytesToRead);

        currentTuple++; // Move to next slot
        return bytesToRead;
    }

    @Override
    public boolean hasNext() {
        return currentTuple < totalTuple;
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
