package socket.server.nio.v2;

import socket.handler.ClientHandler;
import socket.server.nio.v2.handlers.AcceptConnectionHandler;
import socket.server.nio.v2.handlers.MessageReadHandler;
import socket.server.nio.v2.handlers.MessageReplyHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;

public class NonBlockingServer {

    static int port = 8080;

    enum SocketEvent {
        Accept,
        Read,
        Write,
        NotSupported;
    }

    public static void main(String[] args) throws Exception {

        ServerSocketChannel server = startServer(port);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        var pendingData = new HashMap<SocketChannel, Queue<ByteBuffer>>();

        var handlers = ofEntries(
                entry(SocketEvent.Accept, new AcceptConnectionHandler(pendingData)),
                entry(SocketEvent.Read, new MessageReadHandler(pendingData)),
                entry(SocketEvent.Write, new MessageReplyHandler(pendingData))
        );

        System.out.println(String.format("Welcome to %s", NonBlockingServer.class));

        while (true) {
            try {
                selector.select();

                var keys = selector.selectedKeys();
                for (var keyItr = keys.iterator(); keyItr.hasNext(); ) {
                    var key = keyItr.next();
                    if (!key.isValid()) continue;
                    keyItr.remove(); // This is must to clean old keys otherwise keeps getting it back

                    if (key.isAcceptable()) {
                        handlers.get(SocketEvent.Accept).handle(key);
                    }
                    if (key.isReadable()) {
                        handlers.get(SocketEvent.Read).handle(key);
                    }
                    if (key.isWritable()) {
                        handlers.get(SocketEvent.Write).handle(key);
                    }
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static void processEvent(Map<SocketEvent, ClientHandler<SelectionKey>> handlers, SelectionKey event) {
        var handler = handlers.get(toEventType(event));
        try {
            handler.handle(event);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Unable to process event" );
        }
    }

    private static SocketEvent toEventType(SelectionKey selectionKey) {
        var value = SocketEvent.Accept;
        if (selectionKey.isAcceptable()) {
            value = SocketEvent.Accept;
        }
        if (selectionKey.isReadable()) {
            value = SocketEvent.Read;
        }
        if (selectionKey.isWritable()) {
            value = SocketEvent.Write;
        }

        return value;
    }


    private static ServerSocketChannel startServer(int port) {

        try {
            var server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);
            return server;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
