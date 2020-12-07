package query.timeseries.sst.disk;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class PHeaderV1 {
    public byte version;
    public int pageSize;
    public int recordSize;
    public int noOfPages;

    public static int MESSAGE_SIZE = 1 + 4 + 4 + 4;

    public void nextPage() {
        noOfPages++;
    }

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
        buffer.put(version);
        buffer.putInt(pageSize);
        buffer.putInt(recordSize);
        buffer.putInt(noOfPages);
        return buffer.array();
    }

    public PHeaderV1 fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.version = buffer.get();
        this.pageSize = buffer.getInt();
        this.recordSize = buffer.getInt();
        this.noOfPages = buffer.getInt();
        return this;
    }


}
