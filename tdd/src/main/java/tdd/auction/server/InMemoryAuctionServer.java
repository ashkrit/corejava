package tdd.auction.server;

import tdd.auction.AuctionEvents;
import tdd.auction.model.Bid;
import tdd.auction.model.Item;

import static java.util.Optional.ofNullable;

public class InMemoryAuctionServer implements AuctionServer {

    private final AuctionEvents consumers;
    private Item item;

    public InMemoryAuctionServer(AuctionEvents consumer) {
        this.consumers = consumer;
    }

    @Override
    public void join(String item, String bidder) {

        if (isItemOnSale(item)) {
            consumers.onJoin(item, bidder, this.item.price());
        } else {
            consumers.onNoAuction(item);
        }
    }

    private boolean isItemOnSale(String itemToCheck) {
        return ofNullable(item)
                .map(Item::itemName)
                .filter(item -> item.equals(itemToCheck))
                .isPresent();
    }

    @Override
    public void auctionClosed() {
        consumers.onLost();
    }


    @Override
    public void startSelling(String item, int price) {
        this.item = Item.of(item, price);
    }

    @Override
    public void bid(Bid bid) {
        if (isItemOnSale(bid.itemName())) {
            consumers.onPriceChanged(bid);
        }
    }

}
