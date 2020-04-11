package tdd.auction.main;

import tdd.auction.AuctionEventConsumer;
import tdd.auction.AuctionServer;
import tdd.auction.ConsoleOutputAction;

public class AuctionClientApp {

    public static void main(String[] args) {

        String auctionItem = "PICASO-Guernica(1937)";

        AuctionServer server = new AuctionServer();

        server.startSelling(auctionItem, 9990);
        server.join(auctionItem, "X Corp", new AuctionEventConsumer(new ConsoleOutputAction()));
        server.close();

        server.disconnect();

    }
}
