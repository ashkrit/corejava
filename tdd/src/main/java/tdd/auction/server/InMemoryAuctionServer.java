package tdd.auction.server;

import tdd.auction.AuctionEvents;
import tdd.auction.model.Bid;
import tdd.auction.model.Item;

import java.util.HashSet;
import java.util.Set;

import static java.util.Optional.ofNullable;

public class InMemoryAuctionServer implements AuctionServer {

    private final Set<AuctionEvents> consumers = new HashSet<>();
    private Item item;

    public InMemoryAuctionServer() {

    }

    @Override
    public void join(String item, String bidder, AuctionEvents consumer) {
        registerConsumer(consumer);

        if (isItemOnSale(item)) {
            consumer.onJoin(item, bidder, this.item.price());
        } else {
            consumer.onNoAuction(item);
        }
    }

    private void registerConsumer(AuctionEvents consumer) {
        consumers.add(consumer);
    }

    private boolean isItemOnSale(String itemToCheck) {
        return ofNullable(item)
                .map(Item::itemName)
                .filter(item -> item.equals(itemToCheck))
                .isPresent();
    }

    @Override
    public void auctionClosed() {
        consumers.forEach(AuctionEvents::onLost);
    }


    @Override
    public void startSelling(String item, int price) {
        this.item = Item.of(item, price);
    }

    @Override
    public void bid(Bid bid) {
        if (isItemOnSale(bid.itemName())) {
            consumers.forEach(consumer -> consumer.onPriceChanged(bid));
        }
    }

}
