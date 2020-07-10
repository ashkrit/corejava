package encoding.builder.binary;

import net.openhft.chronicle.bytes.Bytes;
import net.openhft.chronicle.wire.RawWire;
import net.openhft.chronicle.wire.Wire;

import java.nio.ByteBuffer;

public class ChronicleTradeRecordBuilder {

    public static Object newTrade(Object[] row) {
        long tradeId = (Long) row[0];
        long customerId = (Long) row[1];
        String exchange = row[2].toString();
        String tradeType = row[3].toString();
        String symbol = row[4].toString();
        int qty = (Integer) row[5];

        Bytes<ByteBuffer> bytes = Bytes.elasticByteBuffer();
        Wire wire = createWriteFormat(bytes);

        wire.write(() -> "tradeId").int64(tradeId)
                .write(() -> "customerId").int64(customerId)
                .write(() -> "exchange").text(exchange)
                .write(() -> "tradeType").text(tradeType)
                .write(() -> "symbol").text(symbol)
                .write(() -> "qty").int32(qty);

        return bytes;
    }

    public static byte[] toBytes(Object encoder) {
        return ((Bytes<ByteBuffer>) encoder).toByteArray();
    }

    public static Object fromBytes(byte[] b) {
        Bytes<ByteBuffer> buffer = Bytes.elasticByteBuffer(b.length);
        buffer.write(b);
        return createWriteFormat(buffer);
    }

    private static Wire createWriteFormat(Bytes<ByteBuffer> bytes) {
        return new RawWire(bytes);
    }
}
