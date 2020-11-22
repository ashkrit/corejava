package query.page.write;

import query.page.PageOffSets;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;

import static java.time.Instant.ofEpochMilli;
import static java.time.LocalDateTime.ofInstant;
import static java.time.ZoneId.systemDefault;

/**
 * Layout Details
 * Page Contains 2 Sections
 * Header
 * Data
 * <p>
 * Header Contains Version(Byte),Page No(Int),No Of tuple(Int)
 * Data contains 2 section one is tuple data and other one is slot array that contains tuple size details
 * .........................................
 * {VERSION}{PAGE NO}{No Of Tuple}
 * {Tuple 1}{Tuple 2}
 * <p>
 * {OffSet 2}{OffSet 1}
 * ...........................................
 * <p>
 * [.....
 * <p>
 * 20,6,7]
 */

public class WritableSlotPage implements WritePage {

    private final byte[] data;
    private final ByteBuffer buffer;

    private byte version;
    private int pageNumber;
    private long createdTs;

    private int writeTupleIndex = 0;
    private int dataWriteIndex = PageOffSets.DATA_OFFSET;


    public WritableSlotPage(int pageSize, byte version, int pageNumber, long createdTs) {
        this.data = new byte[pageSize];
        this.version = version;
        this.pageNumber = pageNumber;
        this.createdTs = createdTs;
        this.buffer = ByteBuffer.wrap(data);
    }


    @Override
    public byte[] commit() {
        writeHeaders();
        return data;
    }

    public void writeHeaders() {
        buffer.put(PageOffSets.PAGE_VERSION, version);
        buffer.putInt(PageOffSets.PAGE_NUMBER, pageNumber);
        buffer.putLong(PageOffSets.CREATED_TS, createdTs);
        buffer.putInt(PageOffSets.NO_OF_TUPLE, writeTupleIndex);
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
    public int noOfTuple() {
        return writeTupleIndex;
    }

    @Override
    public int capacity() {
        return slotOffSet() - dataWriteIndex;
    }

    @Override
    public LocalDateTime createdTime() {
        return ofInstant(ofEpochMilli(createdTs), systemDefault());
    }

    /**
     * - Write data offset in slot array ( 4 bytes) from tail
     * - Write data from header direction
     * - Move to next data write position
     * - Move to next slot
     */
    @Override
    public int write(byte[] bytes) {

        if (!hasCapacity(bytes.length + 4)) {
            return -1;
        }

        int offset = dataWriteIndex;
        for (byte b : bytes) {
            buffer.put(offset++, b);
        }

        nextTuple();
        int slotIndex = slotOffSet();
        buffer.putInt(slotIndex, bytes.length); // Write in slot array
        dataWriteIndex = offset;
        return bytes.length;
    }

    private void nextTuple() {
        writeTupleIndex++;
    }

    private boolean hasCapacity(int bytesRequired) {
        return capacity() > bytesRequired;
    }

    private int slotOffSet() {
        return data.length - writeTupleIndex * 4;
    }

    @Override
    public String toString() {
        return String.format("SlotPage (CreatedAt: %s;Page: %s;Version: %s;Tuple Count: %s;Capacity: %sBytes)", createdTime(), pageNumber, version, writeTupleIndex, capacity());
    }

}
