package query.page;

import java.nio.ByteBuffer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SlotPage {

    private final int pageSize;
    private final byte[] data;
    private short version;
    private int pageNumber;
    private int noOfTuple;
    private final ByteBuffer writer;

    public SlotPage(int pageSize) {
        this.pageSize = pageSize;
        this.data = new byte[pageSize];
        writer = ByteBuffer.wrap(data);
    }

    public SlotPage(byte[] data) {
        this.pageSize = data.length;
        this.data = data;
        this.writer = ByteBuffer.wrap(data);
        readHeaders();
    }

    private void readHeaders() {
        version = writer.getShort(PageOffSets.PAGE_VERSION);
        pageNumber = writer.getInt(PageOffSets.PAGE_NUMBER);
        noOfTuple = writer.getInt(PageOffSets.NO_OF_TUPLE);
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
        writer.putShort(PageOffSets.PAGE_VERSION, version);
        writer.putInt(PageOffSets.PAGE_NUMBER, pageNumber);
        writer.putInt(PageOffSets.NO_OF_TUPLE, noOfTuple);
        return data;
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
