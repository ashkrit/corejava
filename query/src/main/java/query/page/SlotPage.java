package query.page;

import java.nio.ByteBuffer;

public class SlotPage {

    private final byte[] data;
    private byte version;
    private int pageNumber;
    private int noOfTuple;
    private final ByteBuffer buffer;
    private int dataWriteIndex = PageOffSets.DATA_OFFSET;
    private int dataReadIndex = PageOffSets.DATA_OFFSET;
    private int readSlot = 0;

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
        noOfTuple = buffer.getInt(PageOffSets.NO_OF_TUPLE);
    }

    public void version(byte version) {
        this.version = version;
    }

    public void pageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void noOfTuple(int noOfTuple) {
        this.noOfTuple = noOfTuple;
    }

    public byte[] commit() {
        writeHeaders();
        return data;
    }

    public void writeHeaders() {
        buffer.put(PageOffSets.PAGE_VERSION, version);
        buffer.putInt(PageOffSets.PAGE_NUMBER, pageNumber);
        buffer.putInt(PageOffSets.NO_OF_TUPLE, noOfTuple);
    }

    public short version() {
        return version;
    }

    public int pageNumber() {
        return pageNumber;
    }

    public int noOfTuple() {
        return noOfTuple;
    }

    public void write(byte[] bytes) {

        /*
            - Write data offset in slot array ( 4 bytes) from tail
            - Write data from header direction
            - Move to next data write position
            - Move to next slot
         */

        //Update data

        int offset = dataWriteIndex;
        for (byte b : bytes) {
            buffer.put(offset++, b);
        }

        noOfTuple++; // Move to next slot
        int slotIndex = data.length - noOfTuple * 4;
        buffer.putInt(slotIndex, bytes.length); // Write in slot array

        System.out.println("Write Slot .." + slotIndex + "(" + bytes.length + ") Starting from " + dataWriteIndex + " for bytes " + bytes.length);
        dataWriteIndex = offset;
    }

    public int read(byte[] readBuffer) {

        int slotIndex = (data.length - (readSlot) * 4) - 4;

        int bytesToRead = buffer.getInt(slotIndex); // Bytes to read from current position
        //System.out.println("Read Slot .." + slotIndex + "(" + bytesToRead + ") Starting from " + dataReadIndex + " for bytes " + bytesToRead);

        buffer.position(dataReadIndex);
        buffer.get(readBuffer, 0, bytesToRead);

        dataReadIndex += bytesToRead; // Move data pointer
        readSlot++; // Move to next slot
        return bytesToRead;
    }
}
