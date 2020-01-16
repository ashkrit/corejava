package socket.handler.channel;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.channels.SocketChannel;

public class BlockingChannelHandler implements ClientHandler<SocketChannel> {

    private final ClientHandler<SocketChannel> handler;

    public BlockingChannelHandler(ClientHandler<SocketChannel> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(SocketChannel sc) throws IOException {
        while (sc.isConnected()) {
            handler.handle(sc);
        }
    }
}
