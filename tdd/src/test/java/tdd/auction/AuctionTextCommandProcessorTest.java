package tdd.auction;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tdd.auction.server.AuctionServer;
import tdd.auction.server.InMemoryAuctionServer;
import tdd.auction.server.TextCommandProcessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tdd.auction.server.TextCommandProcessor.toMessage;

public class AuctionTextCommandProcessorTest {

    AuctionEventConsumer handler = new AuctionEventConsumer(new ConsoleOutputAction());
    AuctionServer auctionServer = new InMemoryAuctionServer(handler);
    TextCommandProcessor textCommand = new TextCommandProcessor(auctionServer);

    @BeforeEach
    public void bootstrapAuctionServer() {
        auctionServer.start();
    }

    @Test
    public void placesBidButLoseAuction() {

        textCommand.onMessage(toMessage("startselling", "itemName-10", "100"));
        textCommand.onMessage(toMessage("join", "itemName-10", "ABC Corp"));
        textCommand.onMessage(toMessage("bid", "ABC Corp", "itemName-10", "110"));
        textCommand.onMessage(toMessage("auctionclosed"));

        assertEquals(110, handler.lastPrice());
        assertEquals(AuctionState.Lost, handler.auctionState());

    }

    @AfterEach
    public void disconnect() {
        auctionServer.disconnect();
    }

}
