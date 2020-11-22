package query.page;

import java.nio.ByteBuffer;

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

    public void version(short version) {
        this.version = version;
    }

    public void pageNo(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void noOfTuple(int noOfTuple) {
        this.noOfTuple = noOfTuple;
    }

    public byte[] commit() {
        writer.putShort(0, version);
        writer.putInt(4, pageNumber);
        writer.putInt(8, noOfTuple);
        return data;
    }

}
