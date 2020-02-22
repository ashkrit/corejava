package socket.handler.channel;

import socket.MessageTransformer;
import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class ReadHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public ReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        var channel = (SocketChannel) selectionKey.channel();
        var buffer = ByteBuffer.allocate(80);
        var read = channel.read(buffer);
        if (read == -1) {
            pendingData.remove(channel);
            channel.close();
            System.out.println("Disconnected while read " + channel);
            return;
        }

        if (read > 0) {
            MessageTransformer.magic(buffer);
            pendingData.get(channel).add(buffer);
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }
}
