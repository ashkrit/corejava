package socket.server.nio;

import socket.handler.channel.MagicChannelHandler;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;

public class SingleThreadPoolingBlockingServer {

    public static void main(String... args) throws IOException {
        var server = ServerSocketChannel.open();
        server.bind(new InetSocketAddress(8080));
        server.configureBlocking(false);

        var handler = new MagicChannelHandler();
        var connections = new ArrayList<SocketChannel>();

        System.out.println(String.format("Welcome to %s", SingleThreadPoolingBlockingServer.class));

        while (true) {
            var clientSocket = server.accept();
            configureClientConnection(connections, clientSocket);
            processMessage(handler, connections);
            cleanupDeadOnes(connections);
        }
    }

    private static void cleanupDeadOnes(ArrayList<SocketChannel> connections) {
        connections.removeIf(connection -> !connection.isConnected());
    }

    private static void processMessage(MagicChannelHandler handler, ArrayList<SocketChannel> connections) throws IOException {
        for (var connection : connections) {
            if (connection.isConnected()) {
                handler.handle(connection);
            }
        }
    }

    private static void configureClientConnection(ArrayList<SocketChannel> connections, SocketChannel clientSocket) throws IOException {
        if (clientSocket != null) {
            connections.add(clientSocket);
            clientSocket.configureBlocking(false);
            System.out.println("Connected to " + clientSocket);
        }
    }


}
