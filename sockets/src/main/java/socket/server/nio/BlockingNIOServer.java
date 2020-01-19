package socket.server.nio;

import socket.handler.*;
import socket.handler.channel.BlockingChannelHandler;
import socket.handler.channel.MagicChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.Executors;

public class BlockingNIOServer {

    public static void main(String... args) throws IOException {
        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));

        var handler = new ExecutorServiceHandler(
                new IOExceptionHandler(new PrintingHandler<>(new BlockingChannelHandler(new MagicChannelHandler()))),
                Executors.newCachedThreadPool(),
                (thread, e) -> System.out.println("Error " + thread + " Message " + e));

        System.out.println(String.format("Welcome to %s", BlockingNIOServer.class));
        while (true) {
            var clientSocket = server.accept();
            handler.handle(clientSocket);
        }
    }


}
