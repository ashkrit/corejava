package tdd.auction;

public class AuctionEventHandler {
    private String item;
    private String bidder;
    private int price;

    public String auctionItem() {
        return item;
    }

    public int lastPrice() {
        return price;
    }

    public String auctionState() {
        return "lost";
    }

    public void onJoin(String item, String $, int price) {
        this.item = item;
        this.price = price;
    }
}
