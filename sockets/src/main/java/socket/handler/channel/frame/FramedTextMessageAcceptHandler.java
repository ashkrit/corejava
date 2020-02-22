package socket.handler.channel.frame;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Map;

public class FramedTextMessageAcceptHandler implements ClientHandler<SelectionKey> {
    private final Map<SocketChannel, FrameMessage> frameData;

    public FramedTextMessageAcceptHandler(Map<SocketChannel, FrameMessage> frameData) {
        this.frameData = frameData;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        var serverChannel = (ServerSocketChannel) selectionKey.channel();
        var sc = serverChannel.accept();
        if (sc == null) return; // Not sure why ?
        System.out.println("Connected to " + sc);
        sc.configureBlocking(false);

        frameData.put(sc, new FrameMessage());
        sc.register(selectionKey.selector(), SelectionKey.OP_READ);
    }


}
