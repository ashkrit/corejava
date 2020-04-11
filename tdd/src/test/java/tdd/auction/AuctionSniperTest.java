package tdd.auction;


import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionSniperTest {

    @Test
    public void joinsUntilAuctionCloses() {
        AuctionServer auctionServer = new AuctionServer();

        AuctionEventHandler handler = new AuctionEventHandler();

        auctionServer.join("item-123", "ABC Corp", handler);

        auctionServer.close();

        String itemName = handler.auctionItem();
        int itemPrice = handler.lastPrice();
        String auctionState = handler.auctionState();

        assertEquals("item-123", itemName);
        assertEquals(100, itemPrice);
        assertEquals("lost", auctionState);

    }

}
