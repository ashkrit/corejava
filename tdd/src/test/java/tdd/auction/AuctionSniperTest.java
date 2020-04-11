package tdd.auction;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

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

        String itemOnAuction = "item-123";
        int basePrice = 100;

        auctionServer.startSelling(itemOnAuction, basePrice);
        auctionServer.join(itemOnAuction, "ABC Corp", handler);
        auctionServer.close();

        assertEquals("ABC Corp", handler.bidder());
        assertEquals(itemOnAuction, handler.auctionItem());
        assertEquals(basePrice, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }


    @Test
    public void joinAuctionForAnotherItemAndLoose() {


        String itemOnAuction = "item-567";
        int basePrice = 100;

        auctionServer.startSelling(itemOnAuction, basePrice);
        auctionServer.join(itemOnAuction, "ABC Corp", handler);
        auctionServer.close();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals(itemOnAuction, handler.auctionItem());
        assertEquals(basePrice, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }

    @Test
    public void joinAuctionForItemAtSomeRandomPriceAndLoose() {

        String itemOnAuction = "item-567";
        int basePrice = new Random().nextInt(999);

        auctionServer.startSelling(itemOnAuction, basePrice);
        auctionServer.join(itemOnAuction, "ABC Corp", handler);
        auctionServer.close();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals(itemOnAuction, handler.auctionItem());
        assertEquals(basePrice, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());

    }

    @AfterEach
    public void closeAuctionServer() {
        auctionServer.disconnect();
    }

}
