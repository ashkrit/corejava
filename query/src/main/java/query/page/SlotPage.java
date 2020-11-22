package query.page;

import java.nio.ByteBuffer;

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

public class SlotPage {

    private final byte[] data;
    private byte version;
    private int pageNumber;

    private int writeTupleIndex = 0;
    private int readTupleIndex = 0;

    private final ByteBuffer buffer;
    private int dataWriteIndex = PageOffSets.DATA_OFFSET;
    private int dataReadIndex = PageOffSets.DATA_OFFSET;


    public SlotPage(int pageSize, byte version, int pageNumber) {
        this.data = new byte[pageSize];
        this.version = version;
        this.pageNumber = pageNumber;
        this.buffer = ByteBuffer.wrap(data);
    }

    public SlotPage(byte[] data) {
        this.data = data;
        this.buffer = ByteBuffer.wrap(data);
        readHeaders();
    }

    private void readHeaders() {
        version = buffer.get(PageOffSets.PAGE_VERSION);
        pageNumber = buffer.getInt(PageOffSets.PAGE_NUMBER);
        writeTupleIndex = buffer.getInt(PageOffSets.NO_OF_TUPLE);
    }

    public byte[] commit() {
        writeHeaders();
        return data;
    }

    public void writeHeaders() {
        buffer.put(PageOffSets.PAGE_VERSION, version);
        buffer.putInt(PageOffSets.PAGE_NUMBER, pageNumber);
        buffer.putInt(PageOffSets.NO_OF_TUPLE, writeTupleIndex);
    }

    public short version() {
        return version;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int noOfTuple() {
        return writeTupleIndex;
    }

    /**
     * - Write data offset in slot array ( 4 bytes) from tail
     * - Write data from header direction
     * - Move to next data write position
     * - Move to next slot
     */
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

    public void nextTuple() {
        writeTupleIndex++;
    }

    public boolean hasCapacity(int bytesRequired) {
        return capacity() > bytesRequired;
    }

    public int capacity() {
        return slotOffSet() - dataWriteIndex;
    }

    public int slotOffSet() {
        return data.length - writeTupleIndex * 4;
    }

    public int read(byte[] readBuffer) {

        if (!hasNext()) {
            return -1;
        }
        int slotIndex = (data.length - (readTupleIndex) * 4) - 4;

        int bytesToRead = buffer.getInt(slotIndex); // Bytes to read from current position

        buffer.position(dataReadIndex);
        buffer.get(readBuffer, 0, bytesToRead);

        dataReadIndex += bytesToRead; // Move data pointer
        readTupleIndex++; // Move to next slot
        return bytesToRead;
    }

    public boolean hasNext() {
        return readTupleIndex < writeTupleIndex;
    }

    @Override
    public String toString() {
        return String.format("SlotPage(Page %s;Version %s;Tuple Count %s;Capacity %sBytes)", pageNumber, version, writeTupleIndex, capacity());
    }
}
