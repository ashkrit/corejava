package tdd.auction;

import java.util.HashSet;
import java.util.Set;

public class AuctionServer {

    Set<AuctionEventHandler> handlers = new HashSet<>();

    public AuctionServer() {

    }

    public void join(String item, String bidder, AuctionEventHandler eventHandler) {
        handlers.add(eventHandler);
        eventHandler.onJoin(item, bidder, 100);
    }

    public void close() {
        handlers.forEach(AuctionEventHandler::onLost);
    }

    public void disconnect() {

    }

    public void start() {

    }
}
