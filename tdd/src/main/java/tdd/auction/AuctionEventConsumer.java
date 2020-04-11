package tdd.auction;

import static java.lang.String.format;

public class AuctionEventConsumer {

    private final ConsoleOutputAction action;
    private final AuctionServer auctionServer;

    private String item;
    private String bidder;

    private int price;
    private AuctionState currentState = AuctionState.Joining;
    private String message;

    public AuctionEventConsumer(AuctionServer auctionServer) {
        this(auctionServer, null);
    }

    public AuctionEventConsumer(AuctionServer auctionServer, ConsoleOutputAction action) {
        this.action = action;
        this.auctionServer = auctionServer;
    }

    public String auctionItem() {
        return item;
    }

    public String message() {
        return message;
    }

    public int lastPrice() {
        return price;
    }

    public AuctionState auctionState() {
        return currentState;
    }

    public String bidder() {
        return bidder;
    }

    public void onJoin(String item, String bidder, int price) {
        this.item = item;
        this.price = price;
        this.bidder = bidder;
        this.currentState = AuctionState.Joining;
        if (action != null) {
            action.displayMessage(format("%s Joined auction for %s item and it is trading at %s$", bidder, item, price));
        }

    }

    public void onLost() {
        this.currentState = AuctionState.Lost;
        if (action != null) {
            action.displayMessage(format("%s lost auction", bidder));
        }
    }

    public void onNoAuction(String item) {
        this.currentState = AuctionState.NoAuction;
        this.message = format("No auction going on %s", item);
    }

    public void onPriceChanged(String item, String bidder, int newPrice) {
        this.item = item;
        this.price = newPrice;
        this.bidder = bidder;
        if (action != null) {
            action.displayMessage(format("[%bidder] placed bid for item %s at %s $", bidder, item, newPrice));
        }
    }

    public void placeBid(int increaseAmount) {
        auctionServer.bid(item, bidder, price + increaseAmount);
    }
}
