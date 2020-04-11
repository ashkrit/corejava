package tdd.auction;

import tdd.auction.model.Bid;
import tdd.auction.model.Item;
import tdd.auction.server.AuctionServer;

import static java.lang.String.format;
import static java.util.Optional.ofNullable;

public class AuctionEventConsumer implements AuctionEvents {

    private final ConsoleOutputAction action;
    private final AuctionServer auctionServer;

    private Item item;
    private String bidder;

    private AuctionState currentState = AuctionState.Joining;
    private String message;
    private Bid lastBid;

    public AuctionEventConsumer(AuctionServer auctionServer) {
        this(auctionServer, null);
    }

    public AuctionEventConsumer(AuctionServer auctionServer, ConsoleOutputAction action) {
        this.action = action;
        this.auctionServer = auctionServer;
    }


    public Item auctionItem() {
        return item;
    }

    public String message() {
        return message;
    }

    public int lastPrice() {
        return ofNullable(lastBid)
                .map(Bid::price)
                .orElseGet(() -> item.price());
    }

    public AuctionState auctionState() {
        return currentState;
    }

    public String bidder() {
        return bidder;
    }

    @Override
    public void onJoin(String item, String bidder, int price) {

        this.bidder = bidder;
        this.item = Item.of(item, price);
        this.currentState = AuctionState.Joining;

        if (action != null) {
            action.displayMessage(format("[%s] Joined auction for %s itemName and it is trading at %s$", bidder, item, price));
        }

    }

    @Override
    public void onLost() {
        this.currentState = AuctionState.Lost;
        if (action != null) {
            action.displayMessage(format("[%s] lost auction", bidder));
        }
    }

    @Override
    public void onNoAuction(String item) {
        this.currentState = AuctionState.NoAuction;
        this.message = format("No auction going on %s", item);
    }

    @Override
    public void onPriceChanged(Bid bid) {
        this.lastBid = bid;
        if (action != null) {
            action.displayMessage(format("[%s] placed bid for item %s at %s $", this.lastBid.getBidder(), lastBid.itemName(), lastBid.price()));
        }
    }

    public void placeBid(int increaseAmount) {
        int newPrice = lastPrice() + increaseAmount;
        Item adjustedItem = Item.of(item.itemName(), newPrice);
        auctionServer.bid(new Bid(bidder, adjustedItem));
    }
}
