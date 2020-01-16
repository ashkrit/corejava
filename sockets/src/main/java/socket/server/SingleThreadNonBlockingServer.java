package socket.server;

import socket.handler.channel.MagicChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class SingleThreadNonBlockingServer {

    public static void main(String... args) throws IOException {
        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        var handler = new MagicChannelHandler();
        var connections = new ArrayList<SocketChannel>();

        while (true) {
            var clientSocket = server.accept();
            if (clientSocket != null) {
                connections.add(clientSocket);
                clientSocket.configureBlocking(false);
                System.out.println("Connected to " + clientSocket);
            }

            for (var connection : connections) {
                if (connection.isConnected()) {
                    handler.handle(connection);
                }
            }

            connections.removeIf(connection -> !connection.isConnected());
        }
    }


}
