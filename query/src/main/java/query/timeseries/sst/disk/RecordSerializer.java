package query.timeseries.sst.disk;

import java.nio.ByteBuffer;
import java.util.function.Function;

public class RecordSerializer<V> {
    public final Function<V, byte[]> toBytes;
    public final Function<ByteBuffer, V> fromBytes;
    public final Function<V, String> pk;
    public final int pageSize;

    public RecordSerializer(int pageSize, Function<V, byte[]> toBytes, Function<ByteBuffer, V> fromBytes, Function<V, String> toPk) {
        this.pageSize = pageSize;
        this.toBytes = toBytes;
        this.fromBytes = fromBytes;
        this.pk = toPk;
    }

    public Function<V, byte[]> getToBytes() {
        return toBytes;
    }

    public Function<ByteBuffer, V> getFromBytes() {
        return fromBytes;
    }

    public Function<V, String> getPk() {
        return pk;
    }

    public int getPageSize() {
        return pageSize;
    }
}
