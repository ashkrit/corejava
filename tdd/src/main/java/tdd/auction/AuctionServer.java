package tdd.auction;

import java.util.HashSet;
import java.util.Set;

public class AuctionServer {

    private final Set<AuctionEventHandler> bidders = new HashSet<>();
    private String item;
    private int currentPrice;

    public AuctionServer() {

    }

    public void join(String item, String bidder, AuctionEventHandler eventHandler) {
        bidders.add(eventHandler);
        if (item.equals(this.item)) {
            eventHandler.onJoin(item, bidder, currentPrice);
        } else {
            eventHandler.onNoAuction(item);
        }
    }

    public void close() {
        bidders.forEach(AuctionEventHandler::onLost);
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
