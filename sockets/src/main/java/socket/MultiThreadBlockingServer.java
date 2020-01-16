package socket;

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
            System.out.println(String.format("Connected to client %s, connection so far %s, active %s", clientSocket, noOfConnection, activeConnection));
            handleClient(clientSocket);
        }
    }

    private static void handleClient(Socket clientSocket) {
        Runnable task = () -> {
            try (clientSocket;
                 var in = clientSocket.getInputStream();
                 var out = clientSocket.getOutputStream()) {

                var b = -1;
                while ((b = in.read()) != -1) {
                    out.write(magic(b));
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            } finally {
                activeConnection.decrementAndGet();
                System.out.println(String.format("Disconnected from client %s , active %s", clientSocket, activeConnection));
            }
        };

        new Thread(task).start();

    }

    private static int magic(int b) {
        return Character.isLetter(b) ? b ^ ' ' : b;
    }
}
