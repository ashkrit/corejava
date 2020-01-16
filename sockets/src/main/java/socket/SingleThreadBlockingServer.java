package socket;

import socket.handler.MagicHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class SingleThreadBlockingServer {

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        while (true) {
            var clientSocket = server.accept();
            System.out.println(String.format("Connected to client %s", clientSocket));
            new MagicHandler().handle(clientSocket);
        }
    }

}
