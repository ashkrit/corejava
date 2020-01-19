package socket.handler.channel;

import socket.MessageTransformer;
import socket.handler.ClientHandler;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

public class WorkerPoolReadHandler implements ClientHandler<SelectionKey> {

    private final Map<SocketChannel, Queue<ByteBuffer>> pendingData;
    private final ExecutorService service;
    private final ConcurrentLinkedQueue<Runnable> pendingActions;

    public WorkerPoolReadHandler(Map<SocketChannel, Queue<ByteBuffer>> pendingData, ExecutorService service, ConcurrentLinkedQueue<Runnable> pendingActions) {
        this.pendingData = pendingData;
        this.service = service;
        this.pendingActions = pendingActions;
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
            service.submit(() -> {
                MessageTransformer.magic(buffer);
                pendingData.get(channel).add(buffer);

                pendingActions.add(() -> selectionKey.interestOps(SelectionKey.OP_WRITE));
                selectionKey.selector().wakeup();
            });
        }
    }
}
