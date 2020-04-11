package tdd.auction;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionSniperTest {

    AuctionServer auctionServer = new AuctionServer();

    @BeforeEach
    public void bootstrapAuctionServer() {
        auctionServer.start();
    }

    @Test
    public void joinsUntilAuctionCloses() {

        AuctionEventHandler handler = new AuctionEventHandler();

        auctionServer.join("item-123", "ABC Corp", handler);

        auctionServer.close();

        assertEquals("item-123", handler.auctionItem());
        assertEquals(100, handler.lastPrice());
        assertEquals("lost", handler.auctionState());
    }

    @AfterEach
    public void closeAuctionServer() {
        auctionServer.disconnect();
    }

}
