package socket.server.nio.v2.handlers;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class MessageReplyHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public MessageReplyHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) {

        var channel = (SocketChannel) selectionKey.channel();
        var data = pendingData.get(channel);

        for (var values = data.iterator(); values.hasNext(); ) {
            var buffer = values.next();
            int bytesWritten = safeWrite(channel, buffer);

            if (hasNoBytes(bytesWritten)) {
                cleanup(channel);
                return;
            }
            if (buffer.hasRemaining()) {
                return;
            }
            values.remove();
        }

        prepareForRead(selectionKey);
    }

    private void prepareForRead(SelectionKey selectionKey) {
        selectionKey.interestOps(SelectionKey.OP_READ);
    }

    private void cleanup(SocketChannel channel) {
        try {
            channel.close();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        pendingData.remove(channel);
        System.out.println("Disconnected while write " + channel);
    }

    private boolean hasNoBytes(int bytesWritten) {
        return bytesWritten == -1;
    }

    private int safeWrite(SocketChannel channel, ByteBuffer buffer) {
        try {
            return channel.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }
}
