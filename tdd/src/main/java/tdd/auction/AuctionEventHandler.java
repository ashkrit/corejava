package tdd.auction;

public class AuctionEventHandler {
    private String item;
    private String bidder;
    private int price;
    private AuctionState currentState = AuctionState.Joining;
    private String message;

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
    }

    public void onLost() {
        this.currentState = AuctionState.Lost;
    }

    public void onNoAuction(String item) {
        this.currentState = AuctionState.NoAuction;
        this.message = String.format("No auction going on %s", item);
    }
}
