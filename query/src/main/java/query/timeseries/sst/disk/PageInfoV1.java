package query.timeseries.sst.disk;

import java.nio.ByteBuffer;

public class PageInfoV1 {
    public int pageId;
    public long minValue;
    public long maxValue;
    public long offSet;

    public static int MESSAGE_SIZE = 4 + 8 + 8 + 8;

    public byte[] toBytes() {
        ByteBuffer buffer = ByteBuffer.allocate(MESSAGE_SIZE);
        buffer.putInt(pageId);
        buffer.putLong(offSet);
        buffer.putLong(minValue);
        buffer.putLong(maxValue);
        return buffer.array();
    }

    public PageInfoV1 fromBytes(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        this.pageId = buffer.getInt();
        this.offSet = buffer.getLong();
        this.minValue = buffer.getLong();
        this.maxValue = buffer.getLong();
        return this;
    }


}
