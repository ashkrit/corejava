package tdd.auction;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AuctionSniperTest {

    AuctionServer auctionServer = new AuctionServer();
    AuctionEventConsumer handler = new AuctionEventConsumer();

    @BeforeEach
    public void bootstrapAuctionServer() {
        auctionServer.start();
    }

    @Test
    public void joinsUntilAuctionCloses() {

        auctionServer.startSelling("item-123", 100);
        auctionServer.join("item-123", "ABC Corp", handler);
        auctionServer.close();

        assertEquals("ABC Corp", handler.bidder());
        assertEquals("item-123", handler.auctionItem());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }


    @Test
    public void joinAuctionForAnotherItemAndLoose() {


        auctionServer.startSelling("item-567", 100);
        auctionServer.join("item-567", "ABC Corp", handler);
        auctionServer.close();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals("item-567", handler.auctionItem());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }

    @Test
    public void joinAuctionForItemAtSomeRandomPriceAndLoose() {

        int basePrice = new Random().nextInt(999);

        auctionServer.startSelling("item-567", basePrice);
        auctionServer.join("item-567", "ABC Corp", handler);
        auctionServer.close();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals("item-567", handler.auctionItem());
        assertEquals(basePrice, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());

    }

    @Test
    public void joiningFailsWhenNoAuctionIsGoingOnForItem() {

        auctionServer.join("item-567", "ABC Corp", handler);
        assertEquals(AuctionState.NoAuction, handler.auctionState());
        assertEquals("No auction going on item-567", handler.message());
    }


    @Test
    public void showsEventMessageOnConsole() {

        ByteArrayOutputStream bos = overrideSysOut();

        handler = new AuctionEventConsumer(new ConsoleOutputAction());
        auctionServer.startSelling("item-567", 200);
        auctionServer.join("item-567", "ABC Corp", handler);
        auctionServer.close();

        String[] messages = new String(bos.toByteArray()).split("\r\n");
        assertEquals("ABC Corp Joined auction for item-567 item and it is trading at 200$", messages[0]);
        assertEquals("ABC Corp lost auction", messages[1]);
    }

    @AfterEach
    public void closeAuctionServer() {
        auctionServer.disconnect();
    }

    private ByteArrayOutputStream overrideSysOut() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(bos));
        return bos;
    }

}
