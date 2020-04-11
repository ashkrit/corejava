package tdd.auction.model;

public class Bid {
    private final String bidder;
    private final Item bidItem;

    public Bid(String bidder, Item bidItem) {
        this.bidder = bidder;
        this.bidItem = bidItem;

    }

    public String itemName() {
        return bidItem.itemName();
    }

    public String getBidder() {
        return bidder;
    }

    public int price() {
        return bidItem.price();
    }
}
