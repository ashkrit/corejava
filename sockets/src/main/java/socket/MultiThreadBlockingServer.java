package socket;

import socket.handler.MagicHandler;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicLong;

public class MultiThreadBlockingServer {

    static AtomicLong noOfConnection = new AtomicLong(0);
    static AtomicLong activeConnection = new AtomicLong(0);

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        while (true) {
            var clientSocket = server.accept();
            noOfConnection.incrementAndGet();
            activeConnection.incrementAndGet();
            System.out.println(String.format("Connected to client %s, connection so far %s, active %s",
                    clientSocket, noOfConnection, activeConnection));

            handleClient(clientSocket);
        }
    }

    private static void handleClient(Socket clientSocket) {
        Runnable task = () -> {
            try {
                new MagicHandler().handle(clientSocket);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        new Thread(task).start();

    }


}
