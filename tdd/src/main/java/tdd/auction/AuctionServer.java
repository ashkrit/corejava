package tdd.auction;

import java.util.HashSet;
import java.util.Set;

public class AuctionServer {

    private final Set<AuctionEventConsumer> consumers = new HashSet<>();
    private String item;
    private int currentPrice;

    public AuctionServer() {

    }

    public void join(String item, String bidder, AuctionEventConsumer consumer) {
        consumers.add(consumer);
        if (isItemOnSale(item)) {
            consumer.onJoin(item, bidder, currentPrice);
        } else {
            consumer.onNoAuction(item);
        }
    }

    private boolean isItemOnSale(String item) {
        return item.equals(this.item);
    }

    public void close() {
        consumers.forEach(AuctionEventConsumer::onLost);
    }

    public void disconnect() {

    }

    public void start() {

    }

    public void startSelling(String item, int price) {
        this.item = item;
        this.currentPrice = price;
    }

    public void bid(String item, String bidder, int newPrice) {

        if (isItemOnSale(item)) {
            consumers.forEach(consumer -> {
                consumer.onPriceChanged(item, bidder, newPrice);
            });
        }
    }
}
