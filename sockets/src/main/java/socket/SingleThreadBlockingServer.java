package socket;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class SingleThreadBlockingServer {

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        while (true) {
            var clientSocket = server.accept();
            System.out.println(String.format("Connected to client %s", clientSocket));
            handleClient(clientSocket);
        }
    }

    private static void handleClient(Socket clientSocket) throws IOException {
        try (clientSocket;
             var in = clientSocket.getInputStream();
             var out = clientSocket.getOutputStream()) {

            //in.transferTo(out);
            var b = -1;
            while ((b = in.read()) != -1) {
                out.write(magic(b));
            }
        } finally {
            System.out.println(String.format("Disconnected from client %s", clientSocket));
        }
    }

    private static int magic(int b) {
        return Character.isLetter(b) ? b ^ ' ' : b;
    }
}
