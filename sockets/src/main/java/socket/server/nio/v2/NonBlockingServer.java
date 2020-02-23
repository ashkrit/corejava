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

    private final Map<SocketEvent, ClientHandler<SelectionKey>> handlers;
    private final int serverPort;

    public NonBlockingServer(Map<SocketEvent, ClientHandler<SelectionKey>> handlers, int serverPort) {
        this.handlers = handlers;
        this.serverPort = serverPort;
    }

    public void start() {
        var selector = createSocketSelector(serverPort);
        System.out.println(String.format("Welcome to %s", this.getClass()));
        startEventLoop(selector);
    }

    private void startEventLoop(Selector selector) {
        while (true) {
            try {
                selector.select();

                var keyItr = selector.selectedKeys().iterator();
                while (keyItr.hasNext()) {
                    var key = keyItr.next();
                    if (key.isValid()) {
                        keyItr.remove(); // This is must to clean old keys otherwise keeps getting it back
                        processEvent(key);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Selector createSocketSelector(int port) {
        try {
            var server = ServerSocketChannel.open();
            server.bind(new InetSocketAddress(port));
            server.configureBlocking(false);

            Selector selector = Selector.open();
            server.register(selector, SelectionKey.OP_ACCEPT);
            return selector;

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    enum SocketEvent {
        Accept,
        Read,
        Write,
        NotSupported
    }

    private void processEvent(SelectionKey event) {
        var handler = handlers.get(toEventType(event));
        consumeException(event, handler);
    }

    private static void consumeException(SelectionKey t, ClientHandler<SelectionKey> function) {
        try {
            function.handle(t);
        } catch (Exception oops) {
            oops.printStackTrace();
        }
    }

    private SocketEvent toEventType(SelectionKey selectionKey) {
        var value = SocketEvent.NotSupported;
        if (selectionKey.isAcceptable()) {
            return SocketEvent.Accept;
        } else if (selectionKey.isReadable()) {
            return SocketEvent.Read;
        } else if (selectionKey.isWritable()) {
            return SocketEvent.Write;
        }
        return value;
    }


    public static void main(String[] args) {

        var buffer = new HashMap<SocketChannel, Queue<ByteBuffer>>();
        var handlers = ofEntries(
                entry(SocketEvent.Accept, new AcceptConnectionHandler(buffer)),
                entry(SocketEvent.Read, new MessageReadHandler(buffer)),
                entry(SocketEvent.Write, new MessageReplyHandler(buffer))
        );
        new NonBlockingServer(handlers, 8080).start();


    }

}
