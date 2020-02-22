package socket.server.nio.v2.handlers;

import socket.MessageTransformer;
import socket.handler.ClientHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class MessageReadHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingMessage;

    public MessageReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingMessage) {
        this.pendingMessage = pendingMessage;
    }

    @Override
    public void handle(SelectionKey selectionKey) {

        var channel = (SocketChannel) selectionKey.channel();
        var buffer = ByteBuffer.allocate(80);

        var read = read(channel, buffer);

        if (isEmpty(read)) {
            cleanUp(channel);
        } else {
            processMessage(selectionKey, channel, buffer);
        }

    }

    private int read(SocketChannel channel, ByteBuffer buffer) {
        try {
            return channel.read(buffer);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void processMessage(SelectionKey selectionKey, SocketChannel channel, ByteBuffer buffer) {

        MessageTransformer.magic(buffer);

        pendingMessage.get(channel).add(buffer);
        selectionKey.interestOps(SelectionKey.OP_WRITE);
        selectionKey.selector().wakeup();
    }

    private void cleanUp(SocketChannel socketChannel) {
        pendingMessage.remove(socketChannel);
        safeClose(socketChannel);
        System.out.println("Disconnected while read " + socketChannel);
    }

    private void safeClose(SocketChannel socketChannel) {
        try {
            socketChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmpty(int bytesRead) {
        return bytesRead == -1;
    }
}
