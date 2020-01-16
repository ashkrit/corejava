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

            //in.transferTo(out);
            var b = -1;
            while ((b = in.read()) != -1) {
                out.write(MessageTransformer.magic(b));
            }
        } finally {
            System.out.println(String.format("Disconnected from client %s", clientSocket));
        }
    }
}
