package tdd.auction;

public class AuctionServer {
    public AuctionServer() {

    }

    public void join(String item, String bidder, AuctionEventHandler eventHandler) {

        eventHandler.onJoin(item, bidder, 100);
    }

    public void close() {

    }

    public void disconnect() {

    }

    public void start() {

    }
}
