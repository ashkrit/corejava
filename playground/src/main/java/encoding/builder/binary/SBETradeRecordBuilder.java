package encoding.builder.binary;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import sbe.persistence.*;

import java.nio.ByteBuffer;

public class SBETradeRecordBuilder {

    public static SBETradeEncoder newTrade(Object[] row) {

        long tradeId = (Long) row[0];
        long customerId = (Long) row[1];
        String exchange = row[2].toString();
        String tradeType = row[3].toString();
        String symbol = row[4].toString();
        int qty = (Integer) row[5];

        SBETradeEncoder tradeEncoder = new SBETradeEncoder();
        MessageHeaderEncoder messageHeaderEncoder = new MessageHeaderEncoder();

        ByteBuffer buffer = ByteBuffer.allocate(100);
        MutableDirectBuffer mutableBuffer = new UnsafeBuffer(buffer);
        tradeEncoder.wrapAndApplyHeader(mutableBuffer, 0, messageHeaderEncoder);
        SBETradeEncoder te = tradeEncoder
                .tradeId(tradeId).customerId(customerId)
                .tradeType(SBETradeType.valueOf(tradeType)).qty(qty)
                .symbol(symbol).exchange(exchange);
        return te;
    }

    public static byte[] toBytes(Object o) {
        SBETradeEncoder t = (SBETradeEncoder) o;
        byte[] bytes = new byte[t.limit()];
        t.buffer().getBytes(0, bytes);
        return bytes;
    }

    public static Object fromBytes(byte[] b) {

        SBETradeDecoder tradeDecoder = new SBETradeDecoder();
        MessageHeaderDecoder messageHeaderDecoder = new MessageHeaderDecoder();

        MutableDirectBuffer directBuffer = new UnsafeBuffer(ByteBuffer.wrap(b));

        messageHeaderDecoder.wrap(directBuffer, 0);
        tradeDecoder.wrap(directBuffer, MessageHeaderDecoder.ENCODED_LENGTH, messageHeaderDecoder.blockLength(), messageHeaderDecoder.version());

        return tradeDecoder;
    }

}
