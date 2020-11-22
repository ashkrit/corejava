package query.page;

import java.nio.ByteBuffer;

public class SlotPage {

    private final byte[] data;
    private short version;
    private int pageNumber;
    private int noOfTuple;
    private final ByteBuffer buffer;

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
        version = buffer.getShort(PageOffSets.PAGE_VERSION);
        pageNumber = buffer.getInt(PageOffSets.PAGE_NUMBER);
        noOfTuple = buffer.getInt(PageOffSets.NO_OF_TUPLE);
    }

    public void version(short version) {
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
        buffer.putShort(PageOffSets.PAGE_VERSION, version);
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
}
