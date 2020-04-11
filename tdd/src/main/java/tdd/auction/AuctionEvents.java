package tdd.auction;

import tdd.auction.model.Bid;

public interface AuctionEvents {
    void onJoin(String item, String bidder, int price);

    void onLost();

    void onNoAuction(String item);

    void onPriceChanged(Bid bid);
}
