package socket.handler.channel;

import socket.MessageTransformer;
import socket.handler.ClientHandler;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class MagicChannelHandler implements ClientHandler<SocketChannel> {
    @Override
    public void handle(SocketChannel sc) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        int bytesRed = sc.read(buffer);
        if (bytesRed == -1) {
            sc.close();
        }

        if (bytesRed > 0) {
            MessageTransformer.magic(buffer);
            while (buffer.hasRemaining()) {
                sc.write(buffer);
            }
            buffer.compact();
        }
    }
}
