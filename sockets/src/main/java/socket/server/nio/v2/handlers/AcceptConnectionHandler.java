package socket.server.nio.v2.handlers;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class AcceptConnectionHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public AcceptConnectionHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) {

        var serverChannel = (ServerSocketChannel) selectionKey.channel();
        var sc = Optional.ofNullable(acceptConnection(serverChannel));
        sc.ifPresent(channel -> this.configureChannel(channel, selectionKey));
    }

    private SocketChannel acceptConnection(ServerSocketChannel serverChannel) {
        try {
            return serverChannel.accept();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void configureChannel(SocketChannel channel, SelectionKey selectionKey) {
        try {
            System.out.println("Connected to " + channel);
            channel.configureBlocking(false);

            pendingData.put(channel, new ConcurrentLinkedQueue<>());

            channel.register(selectionKey.selector(), SelectionKey.OP_READ);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
