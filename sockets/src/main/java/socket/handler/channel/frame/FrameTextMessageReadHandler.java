package socket.handler.channel.frame;

import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;

public class FrameTextMessageReadHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, FrameMessage> frameData;
    private final Map<SocketChannel, Queue<ByteBuffer>> pendingOutputData;

    public FrameTextMessageReadHandler(Map<SocketChannel, FrameMessage> pendingData, Map<SocketChannel, Queue<ByteBuffer>> output) {
        this.frameData = pendingData;
        this.pendingOutputData = output;
    }

    @Override
    public void handle(SelectionKey selectionKey) throws IOException {
        var channel = (SocketChannel) selectionKey.channel();
        var data = frameData.get(channel);

        if (data == null) return;

        if (data.hasMessageLength()) {
            readMessageBody(selectionKey, channel, data);
        } else {
            readMessageLength(channel, data);
        }
    }

    private void readMessageBody(SelectionKey selectionKey, SocketChannel channel, FrameMessage data) throws IOException {
        var buffer = ByteBuffer.allocate(50);
        var read = channel.read(buffer);
        if (read == -1) {
            cleanUp(channel);
            return;
        }
        if (read > 0) {
            collectMessage(selectionKey, channel, data, buffer, read);
        }
    }

    private void readMessageLength(SocketChannel channel, FrameMessage data) throws IOException {
        var buffer = ByteBuffer.allocate(5);
        var read = channel.read(buffer);
        if (read == -1) {
            cleanUp(channel);
            return;
        }
        if (read > 0) {
            buffer.flip();
            String s = new String(buffer.array()).trim();
            data.messageLength(Integer.parseInt(s));
        }
    }

    private void collectMessage(SelectionKey selectionKey, SocketChannel channel, FrameMessage data, ByteBuffer buffer, int read) {
        buffer.flip();
        data.append(new String(buffer.array()).trim(), read);
        if (data.isCompleteMessage()) {

            //Do some processing

            pendingOutputData.put(channel, new ArrayDeque<>());
            pendingOutputData.get(channel).add(data.asBuffer());
            data.reset();
            selectionKey.interestOps(SelectionKey.OP_WRITE);
        }
    }

    private void cleanUp(SocketChannel channel) throws IOException {
        frameData.remove(channel);
        channel.close();
        System.out.println("Disconnected while read " + channel);
    }
}
