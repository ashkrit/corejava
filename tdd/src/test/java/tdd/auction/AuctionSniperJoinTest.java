package tdd.auction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.auction.server.AuctionServer;
import tdd.auction.server.InMemoryAuctionServer;
import tdd.auction.server.TextCommandProcessor;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tdd.auction.server.TextCommandProcessor.toMessage;

public class AuctionSniperJoinTest {

    AuctionEventConsumer handler = new AuctionEventConsumer(new ConsoleOutputAction());
    AuctionServer auctionServer = new InMemoryAuctionServer(handler);
    TextCommandProcessor textCommand = new TextCommandProcessor(auctionServer);

    @BeforeEach
    public void bootstrapAuctionServer() {
        auctionServer.start();
    }

    @Test
    public void joinsUntilAuctionCloses() {

        textCommand.onMessage(toMessage("startselling", "itemName-123", "100"));
        textCommand.onMessage(toMessage("join", "itemName-123", "ABC Corp"));
        textCommand.onMessage(toMessage("auctionclosed"));

        assertEquals("ABC Corp", handler.bidder());
        assertEquals("itemName-123", handler.auctionItem().itemName());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }


    @Test
    public void joinAuctionForAnotherItemAndLoose() {


        auctionServer.startSelling("itemName-567", 100);
        auctionServer.join("itemName-567", "ABC Corp");
        auctionServer.auctionClosed();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals("itemName-567", handler.auctionItem().itemName());
        assertEquals(100, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());
    }

    @Test
    public void joinAuctionForItemAtSomeRandomPriceAndLoose() {

        int basePrice = new Random().nextInt(999);

        auctionServer.startSelling("itemName-567", basePrice);
        auctionServer.join("itemName-567", "ABC Corp");
        auctionServer.auctionClosed();


        assertEquals("ABC Corp", handler.bidder());
        assertEquals("itemName-567", handler.auctionItem().itemName());
        assertEquals(basePrice, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());

    }

    @Test
    public void joiningFailsWhenNoAuctionIsGoingOnForItem() {

        auctionServer.join("itemName-567", "ABC Corp");
        assertEquals(AuctionState.NoAuction, handler.auctionState());
        assertEquals("No auction going on itemName-567", handler.message());
    }


    @Test
    public void showsEventMessageOnConsole() {

        ByteArrayOutputStream bos = overrideSysOut();

        auctionServer.startSelling("itemName-567", 200);
        auctionServer.join("itemName-567", "ABC Corp");
        auctionServer.auctionClosed();

        String[] messages = new String(bos.toByteArray()).split("\r\n");
        assertEquals("[ABC Corp] Joined auction for itemName-567 itemName and it is trading at 200$", messages[0]);
        assertEquals("[ABC Corp] lost auction", messages[1]);
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
