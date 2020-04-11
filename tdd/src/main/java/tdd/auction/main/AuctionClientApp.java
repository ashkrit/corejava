package tdd.auction.main;

import tdd.auction.AuctionEventConsumer;
import tdd.auction.ConsoleOutputAction;
import tdd.auction.server.AuctionServer;
import tdd.auction.server.InMemoryAuctionServer;

public class AuctionClientApp {

    public static void main(String[] args) {

        String auctionItem = "PICASO-Guernica(1937)";

        AuctionEventConsumer consumer = new AuctionEventConsumer(new ConsoleOutputAction());
        AuctionServer server = new InMemoryAuctionServer(consumer);

        server.startSelling(auctionItem, 9990);
        server.join(auctionItem, "X Corp");
        server.bid(consumer.placeBid(10));

        server.auctionClosed();

        server.disconnect();

    }
}
