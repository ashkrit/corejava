package socket;

import java.io.IOException;
import java.net.ServerSocket;

public class SingleThreadBlockingServer {

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        while (true) {
            var clientSocket = server.accept();

            try (var in = clientSocket.getInputStream();
                 var out = clientSocket.getOutputStream()) {

                //in.transferTo(out);
                var b = -1;
                while ((b = in.read()) != -1) {
                    out.write(magic(b));
                }
            }
        }
    }

    private static int magic(int b) {
        return Character.isLetter(b) ? b ^ ' ' : b;
    }
}
