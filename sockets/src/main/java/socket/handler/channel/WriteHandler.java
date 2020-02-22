package socket.handler.channel;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;

public class WriteHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;

    public WriteHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData) {
        this.pendingData = pendingData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        var channel = (SocketChannel) selectionKey.channel();
        var data = pendingData.get(channel);

        var values = data.iterator();

        while (values.hasNext()) {
            var buffer = values.next();
            int bytesWritten = -1;

            try {
                bytesWritten = channel.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (bytesWritten == -1) {
                channel.close();
                pendingData.remove(channel);
                System.out.println("Disconnected while write " + channel);
                return;
            }

            if (buffer.hasRemaining()) {
                return;
            }
            values.remove();
        }
        //pendingData.remove(channel);
        selectionKey.interestOps(SelectionKey.OP_READ);
    }
}
