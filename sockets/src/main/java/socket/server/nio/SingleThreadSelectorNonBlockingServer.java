package socket.server.nio;

import socket.handler.channel.AcceptHandler;
import socket.handler.channel.MagicChannelHandler;
import socket.handler.channel.ReadHandler;
import socket.handler.channel.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

public class SingleThreadSelectorNonBlockingServer {

    public static void main(String... args) throws IOException {
        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        var pendingData = new HashMap<SocketChannel, Queue<ByteBuffer>>();

        var acceptHandler = new AcceptHandler(pendingData);
        var readHandler = new ReadHandler(pendingData);
        var writeHandler = new WriteHandler(pendingData);
        System.out.println(String.format("Welcome to %s", SingleThreadSelectorNonBlockingServer.class));

        while (true) {
            try {
                selector.select();
                var keys = selector.selectedKeys();
                for (var keyItr = keys.iterator(); keyItr.hasNext(); ) {
                    var key = keyItr.next();
                    if (!key.isValid()) continue;

                    if (key.isAcceptable()) {
                        acceptHandler.handle(key);
                    }
                    if (key.isReadable()) {
                        readHandler.handle(key);
                    }
                    if (key.isWritable()) {
                        writeHandler.handle(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
