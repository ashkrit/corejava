package socket.handler.channel;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

public class AcceptHandler implements ClientHandler<SelectionKey> {
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        var serverChannel = (ServerSocketChannel) selectionKey.channel();
        var sc = serverChannel.accept();
        System.out.println("Connected to " + sc);
        if (sc == null) return;
        sc.configureBlocking(false);

        pendingData.put(sc, new ArrayDeque<>());

        sc.register(selectionKey.selector(), SelectionKey.OP_READ);
    }
}
