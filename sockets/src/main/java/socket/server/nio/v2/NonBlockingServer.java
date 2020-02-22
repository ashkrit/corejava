package socket.server.nio.v2;

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
import java.util.Optional;
import java.util.Queue;

public class NonBlockingServer {

    static int port = 8080;

    public static void main(String[] args) throws Exception {

        ServerSocketChannel server = startServer(port);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        var pendingData = new HashMap<SocketChannel, Queue<ByteBuffer>>();

        var acceptHandler = new AcceptConnectionHandler(pendingData);
        var readHandler = new MessageReadHandler(pendingData);
        var writeHandler = new MessageReplyHandler(pendingData);
        System.out.println(String.format("Welcome to %s", NonBlockingServer.class));

        while (true) {
            try {
                selector.select();

                var keys = selector.selectedKeys();
                for (var itr = keys.iterator(); itr.hasNext(); ) {

                    var key = itr.next();

                    if (notValid(key)) continue;

                    itr.remove();

                    Optional<SelectionKey> keyType = Optional.of(key);
                    keyType.filter(SelectionKey::isAcceptable).ifPresent(acceptHandler::handle);
                    keyType.filter(SelectionKey::isReadable).ifPresent(readHandler::handle);
                    keyType.filter(SelectionKey::isWritable).ifPresent(writeHandler::handle);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private static boolean notValid(SelectionKey key) {
        return !key.isValid();
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
