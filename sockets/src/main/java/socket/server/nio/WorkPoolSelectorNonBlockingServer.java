package socket.server.nio;

import socket.handler.channel.AcceptHandler;
import socket.handler.channel.ReadHandler;
import socket.handler.channel.WorkerPoolReadHandler;
import socket.handler.channel.WriteHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class WorkPoolSelectorNonBlockingServer {

    public static void main(String... args) throws IOException {
        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        Selector selector = Selector.open();
        server.register(selector, SelectionKey.OP_ACCEPT);

        var service = Executors.newFixedThreadPool(5);
        var pendingActions = new ConcurrentLinkedQueue<Runnable>();
        var pendingData = new HashMap<SocketChannel, Queue<ByteBuffer>>();

        var acceptHandler = new AcceptHandler(pendingData);
        var readHandler = new WorkerPoolReadHandler(pendingData, service, pendingActions);
        var writeHandler = new WriteHandler(pendingData);
        System.out.println(String.format("Welcome to %s", WorkPoolSelectorNonBlockingServer.class));


        while (true) {
            try {
                selector.select();
                processPendingActions(pendingActions);
                var keys = selector.selectedKeys();
                for (var keyItr = keys.iterator(); keyItr.hasNext(); ) {
                    var key = keyItr.next();
                    if (!key.isValid()) continue;
                    keyItr.remove();

                    if (key.isAcceptable()) {
                        acceptHandler.handle(key);
                    }
                    if (key.isReadable()) {
                        readHandler.handle(key);
                    }
                    if (key.isWritable()) {
                        writeHandler.handle(key);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static void processPendingActions(Queue<Runnable> pendingActions) {
        for (var itr = pendingActions.iterator(); itr.hasNext(); ) {
            itr.next().run();
            itr.remove();
        }
    }

}
