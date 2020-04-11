package tdd.auction;

import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;

public class AuctionServer {

    private final Set<AuctionEventConsumer> consumers = new HashSet<>();
    private BidItem bidItem;

    public AuctionServer() {

    }

    public void join(String item, String bidder, AuctionEventConsumer consumer) {
        registerConsumer(consumer);

        if (isItemOnSale(item)) {
            consumer.onJoin(item, bidder, bidItem.price());
        } else {
            consumer.onNoAuction(item);
        }
    }

    private void registerConsumer(AuctionEventConsumer consumer) {
        consumers.add(consumer);
    }

    private boolean isItemOnSale(String itemToCheck) {
        return ofNullable(bidItem)
                .map(BidItem::item)
                .filter(item -> item.equals(itemToCheck))
                .isPresent();
    }

    public void close() {
        consumers.forEach(AuctionEventConsumer::onLost);
    }

    public void disconnect() {

    }

    public void start() {

    }

    public void startSelling(String item, int price) {
        this.bidItem = new BidItem(item, price);
    }

    public void bid(String item, String bidder, int newPrice) {
        if (isItemOnSale(item)) {
            consumers.forEach(consumer -> consumer.onPriceChanged(item, bidder, newPrice));
        }
    }

    static class BidItem {
        final String item;
        final int price;

        BidItem(String item, int price) {
            this.item = item;
            this.price = price;
        }

        public String item() {
            return item;
        }

        public int price() {
            return price;
        }
    }
}
