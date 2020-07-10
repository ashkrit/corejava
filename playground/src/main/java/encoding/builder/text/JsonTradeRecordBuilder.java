package encoding.builder.text;

import com.google.gson.Gson;
import encoding.builder.text.JsonTrade.JsonTradeType;

public class JsonTradeRecordBuilder {

    public static JsonTrade newTrade(Object[] row) {

        long tradeId = (Long) row[0];
        long customerId = (Long) row[1];
        String exchange = row[2].toString();
        String tradeType = row[3].toString();
        String symbol = row[4].toString();
        int qty = (Integer) row[5];

        JsonTrade trade = new JsonTrade();

        trade.setTradeId(tradeId);
        trade.setCustomerId(customerId);
        trade.setExchange(exchange);
        trade.setTradeType(JsonTradeType.valueOf(tradeType));
        trade.setSymbol(symbol);
        trade.setQty(qty);
        return trade;
    }

    public static byte[] toBytes(JsonTrade t) {
        return new Gson().toJson(t).getBytes();
    }

    public static JsonTrade fromBytes(byte[] b) {
        return new Gson().fromJson(new String(b), JsonTrade.class);
    }

}
