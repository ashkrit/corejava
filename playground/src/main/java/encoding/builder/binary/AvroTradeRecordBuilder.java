package encoding.builder.binary;

import model.avro.Trade;
import model.avro.TradeType;

import java.io.IOException;
import java.io.UncheckedIOException;

public class AvroTradeRecordBuilder {

    public static Trade newTrade(Object[] row) {
        long tradeId = (Long) row[0];
        long customerId = (Long) row[1];
        String exchange = row[2].toString();
        String tradeType = row[3].toString();
        String symbol = row[4].toString();
        int qty = (Integer) row[5];

        Trade trade = Trade.newBuilder()
                .setTradeId(tradeId).setCustomerId(customerId)
                .setExchange(exchange).setTradeType(TradeType.valueOf(tradeType))
                .setSymbol(symbol).setQty(qty)
                .build();
        return trade;
    }

    public static byte[] toBytes(Trade t) {
        try {
            return Trade.getEncoder().encode(t).array();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public static Trade fromBytes(byte[] b) {
        try {
            return Trade.getDecoder().decode(b);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
