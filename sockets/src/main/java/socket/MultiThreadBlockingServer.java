package socket;

import socket.handler.MagicHandler;
import socket.handler.PrintingHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadBlockingServer {

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        var handler = new PrintingHandler<>(new MagicHandler());
        while (true) {
            var clientSocket = server.accept();
            handleClient(clientSocket, handler);
        }
    }

    private static void handleClient(Socket clientSocket, PrintingHandler<Socket> handler) {
        new Thread(() -> {
            try {
                handler.handle(clientSocket);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).start();

    }


}
