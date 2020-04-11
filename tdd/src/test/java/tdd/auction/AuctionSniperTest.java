package tdd.auction;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionSniperTest {

    AuctionServer auctionServer = new AuctionServer();
    AuctionEventHandler handler = new AuctionEventHandler();

    @BeforeEach
    public void bootstrapAuctionServer() {
        auctionServer.start();
    }

    @Test
    public void joinsUntilAuctionCloses() {

        auctionServer.join("item-123", "ABC Corp", handler);

        auctionServer.close();

        assertEquals("ABC Corp", handler.bidder());
        assertEquals("item-123", handler.auctionItem());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }


    @Test
    public void joinAuctionForAnotherItemAndLoose() {


        auctionServer.join("item-456", "ABC Corp", handler);

        auctionServer.close();

        assertEquals("ABC Corp", handler.bidder());
        assertEquals("item-456", handler.auctionItem());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());

    }

    @AfterEach
    public void closeAuctionServer() {
        auctionServer.disconnect();
    }

}
