package socket.handler;

import socket.MessageTransformer;

import java.io.IOException;
import java.net.Socket;

public class MagicHandler implements ClientHandler<Socket> {
    @Override
    public void handle(Socket clientSocket) throws IOException {
        try (clientSocket;
             var in = clientSocket.getInputStream();
             var out = clientSocket.getOutputStream()) {

            var b = -1;
            while ((b = in.read()) != -1) {
                if (b == 'e') throw new RuntimeException("Something happened");
                out.write(MessageTransformer.magic(b));
            }
        }
    }
}
