package tdd.auction.main;

import tdd.auction.AuctionEventConsumer;
import tdd.auction.AuctionServer;
import tdd.auction.ConsoleOutputAction;

public class AuctionClientApp {

    public static void main(String[] args) {

        String auctionItem = "PICASO-Guernica(1937)";

        AuctionServer server = new AuctionServer();

        server.startSelling(auctionItem, 9990);
        AuctionEventConsumer consumer = new AuctionEventConsumer(server, new ConsoleOutputAction());
        server.join(auctionItem, "X Corp", consumer);
        consumer.placeBid(10);

        server.close();

        server.disconnect();

    }
}
