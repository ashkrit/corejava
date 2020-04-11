package tdd.auction.server;

import tdd.auction.AuctionEventConsumer;
import tdd.auction.model.Bid;

public interface AuctionServer {
    void join(String item, String bidder, AuctionEventConsumer consumer);

    void startSelling(String item, int price);

    void bid(Bid bid);

    void auctionClosed();

    default void disconnect() {
    }

    default void start() {
    }
}
