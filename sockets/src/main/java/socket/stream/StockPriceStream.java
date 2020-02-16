package socket.stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class StockPriceStream {


    public static void main(String[] args) throws InterruptedException {

        var pool = Executors.newScheduledThreadPool(1);
        var symbols = List.of("GOOSY.NQ", "AAPL.NQ", "FB.NQ");

        Flux<StockInfo> producer = Flux.create(fluxSink -> stockPriceSink(pool, symbols, fluxSink));

        producer.subscribe(v -> System.out.println("T1 Value " + new Date() + "\t" + v));

        Thread.sleep(100_000);
    }

    private static void stockPriceSink(ScheduledExecutorService pool, List<String> symbols, FluxSink<StockInfo> fluxSink) {
        System.out.println("Started");
        pool.scheduleAtFixedRate(() -> extractPrices(symbols, fluxSink), 1, 1, TimeUnit.SECONDS);
        System.out.println("Completed");
    }

    private static void extractPrices(List<String> symbols, FluxSink<StockInfo> fluxSink) {
        symbols
                .stream()
                .map(StockPriceStream::getPrice)
                .forEach(s -> fluxSink.next(s));
    }

    private static StockInfo getPrice(String symbol) {
        return new StockInfo(symbol, StockPriceProvider.getPrice(symbol).orElse(-1.0d));
    }

    static class StockInfo {
        private final String symbol;
        private final double price;

        public StockInfo(String symbol, double price) {
            this.symbol = symbol;
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("Stock %s, Price %s", symbol, price);
        }
    }
}
