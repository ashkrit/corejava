package socket.handler.jetty;

import socket.protocol.StockPrice;

public class StockPriceProcessor implements RequestProcessor<StockPrice, StockPrice> {
    @Override
    public StockPrice process(StockPrice input) {
        return input;
    }

    @Override
    public Class<StockPrice> inputType() {
        return StockPrice.class;
    }
}
