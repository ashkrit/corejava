package encoding.builder.text;

import encoding.builder.text.JsonTrade.JsonTradeType;

public class CSVTradeRecordBuilder {

    public static final String TAB = "\t";

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
        return new StringBuilder()
                .append(t.tradeId).append(TAB)
                .append(t.customerId).append(TAB)
                .append(t.exchange).append(TAB)
                .append(t.tradeType.name()).append(TAB)
                .append(t.symbol).append(TAB)
                .append(t.qty)
                .toString().getBytes();
    }

    public static JsonTrade fromBytes(byte[] b) {
        String[] parts = new String(b).split(TAB);
        int index = 0;
        JsonTrade trade = new JsonTrade();
        trade.tradeId = Long.parseLong(parts[index++]);
        trade.customerId = Long.parseLong(parts[index++]);
        trade.exchange = parts[index++];
        trade.tradeType = JsonTradeType.valueOf(parts[index++]);
        trade.symbol = parts[index++];
        trade.qty = Integer.parseInt(parts[index++]);
        return trade;
    }

}
