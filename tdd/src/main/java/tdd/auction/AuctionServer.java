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
        if (item.equals(this.item)) {
            consumer.onJoin(item, bidder, currentPrice);
        } else {
            consumer.onNoAuction(item);
        }
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
}
