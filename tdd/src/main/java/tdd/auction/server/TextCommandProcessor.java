package tdd.auction.server;

import tdd.auction.model.Bid;
import tdd.auction.model.Item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Arrays.asList;
import static java.util.stream.Collectors.joining;

public class TextCommandProcessor {

    private final AuctionServer server;
    private final Map<String, BiFunction<String[], Integer, Void>> functions = new HashMap<String, BiFunction<String[], Integer, Void>>() {{
        put("startselling", (message, offset) -> onStartSelling(message, offset));
        put("join", (message, offset) -> onJoin(message, offset));
        put("bid", (message, offset) -> onBid(message, offset));
        put("auctionclosed", ($, offset) -> onClose());
    }};

    private Void onClose() {
        server.auctionClosed();
        return null;
    }

    public TextCommandProcessor(AuctionServer server) {
        this.server = server;
    }

    public void onMessage(byte[] b) {

        String[] message = new String(b).split("\t");
        int index = 0;
        String messageType = message[index++];
        BiFunction<String[], Integer, Void> f = functions.get(messageType);
        assertFunction(messageType, f);
        f.apply(message, index);

    }

    private void assertFunction(String messageType, BiFunction<String[], Integer, Void> f) {
        if (f == null) {
            throw new IllegalArgumentException("Unable to find mapping for function " + messageType);
        }
    }

    private Void onBid(String[] message, int index) {
        String bidder = message[index++];
        String item = message[index++];
        int price = Integer.parseInt(message[index++]);
        server.bid(new Bid(bidder, Item.of(item, price)));
        return null;
    }

    private Void onJoin(String[] message, int index) {
        String item = message[index++];
        String bidder = message[index++];
        server.join(item, bidder);
        return null;
    }

    private Void onStartSelling(String[] message, int index) {
        String item = message[index++];
        int price = Integer.parseInt(message[index++]);
        server.startSelling(item, price);
        return null;
    }

    public static byte[] toMessage(String... params) {
        return asList(params)
                .stream()
                .collect(joining("\t"))
                .getBytes();
    }

}
