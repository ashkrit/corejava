package socket.server.nio;

import socket.handler.channel.WriteHandler;
import socket.handler.channel.frame.FramedTextMessageAcceptHandler;
import socket.handler.channel.frame.FrameTextMessageReadHandler;
import socket.handler.channel.frame.FrameMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Queue;

public class SingleThreadFrameNonBlockingServer {

    public static void main(String... args) throws IOException {

        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        var pendingData = new HashMap<SocketChannel, FrameMessage>();
        var output = new HashMap<SocketChannel, Queue<ByteBuffer>>();

        var acceptHandler = new FramedTextMessageAcceptHandler(pendingData);
        var readHandler = new FrameTextMessageReadHandler(pendingData, output);
        var writeHandler = new WriteHandler(output);
        System.out.println(String.format("Welcome to %s", SingleThreadFrameNonBlockingServer.class));

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
                selector.selectedKeys().clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
