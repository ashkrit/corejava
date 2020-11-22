package query.page;

import java.nio.ByteBuffer;

public class SlotPage {

    private final byte[] data;
    private byte version;
    private int pageNumber;
    private int totalNumberOfTuple;
    private final ByteBuffer buffer;
    private int dataWriteIndex = PageOffSets.DATA_OFFSET;
    private int dataReadIndex = PageOffSets.DATA_OFFSET;
    private int currentTuple = 0;

    public SlotPage(int pageSize) {
        this.data = new byte[pageSize];
        buffer = ByteBuffer.wrap(data);
    }

    public SlotPage(byte[] data) {
        this.data = data;
        this.buffer = ByteBuffer.wrap(data);
        readHeaders();
    }

    private void readHeaders() {
        version = buffer.get(PageOffSets.PAGE_VERSION);
        pageNumber = buffer.getInt(PageOffSets.PAGE_NUMBER);
        totalNumberOfTuple = buffer.getInt(PageOffSets.NO_OF_TUPLE);
    }

    public void version(byte version) {
        this.version = version;
    }

    public void pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void noOfTuple(int noOfTuple) {
        this.totalNumberOfTuple = noOfTuple;
    }

    public byte[] commit() {
        writeHeaders();
        return data;
    }

    public void writeHeaders() {
        buffer.put(PageOffSets.PAGE_VERSION, version);
        buffer.putInt(PageOffSets.PAGE_NUMBER, pageNumber);
        buffer.putInt(PageOffSets.NO_OF_TUPLE, totalNumberOfTuple);
    }

    public short version() {
        return version;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int noOfTuple() {
        return totalNumberOfTuple;
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

        totalNumberOfTuple++; // Move to next slot
        int slotIndex = slotOffSet();
        buffer.putInt(slotIndex, bytes.length); // Write in slot array
        dataWriteIndex = offset;
        return bytes.length;
    }

    public boolean hasCapacity(int bytesRequired) {
        return capacity() > bytesRequired;
    }

    public int capacity() {
        return slotOffSet() - dataWriteIndex;
    }

    public int slotOffSet() {
        return data.length - totalNumberOfTuple * 4;
    }

    public int read(byte[] readBuffer) {

        if (!hasNext()) {
            return -1;
        }
        int slotIndex = (data.length - (currentTuple) * 4) - 4;

        int bytesToRead = buffer.getInt(slotIndex); // Bytes to read from current position

        buffer.position(dataReadIndex);
        buffer.get(readBuffer, 0, bytesToRead);

        dataReadIndex += bytesToRead; // Move data pointer
        currentTuple++; // Move to next slot
        return bytesToRead;
    }

    public boolean hasNext() {
        return currentTuple < totalNumberOfTuple;
    }
}
