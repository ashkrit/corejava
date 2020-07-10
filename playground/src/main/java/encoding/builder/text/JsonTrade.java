package encoding.builder.text;

public class JsonTrade {
    public long tradeId;
    public long customerId;
    public long qty;
    public JsonTradeType tradeType;
    public String symbol;
    public String exchange;

    public long getTradeId() {
        return tradeId;
    }

    public void setTradeId(long tradeId) {
        this.tradeId = tradeId;
    }

    public long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(long customerId) {
        this.customerId = customerId;
    }

    public long getQty() {
        return qty;
    }

    public void setQty(long qty) {
        this.qty = qty;
    }

    public JsonTradeType getTradeType() {
        return tradeType;
    }

    public void setTradeType(JsonTradeType tradeType) {
        this.tradeType = tradeType;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    @Override
    public String toString() {
        return "{" +
                "tradeId:" + tradeId +
                ",customerId:" + customerId +
                ",qty:" + qty +
                ",tradeType:" + tradeType +
                ",symbol:" + symbol +
                ",exchange:" + exchange +
                "}";
    }

    public enum JsonTradeType {
        Buy,
        Sell
    }
}
