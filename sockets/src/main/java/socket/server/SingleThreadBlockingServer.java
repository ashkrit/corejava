package socket.server;

import socket.handler.IOExceptionHandler;
import socket.handler.MagicHandler;
import socket.handler.PrintingHandler;

import java.io.IOException;
import java.net.ServerSocket;

public class SingleThreadBlockingServer {

    public static void main(String... args) throws IOException {

        var handler = new IOExceptionHandler(new PrintingHandler<>(new MagicHandler()));

        var server = new ServerSocket(8080);
        while (true) {
            var clientSocket = server.accept();
            handler.handle(clientSocket);
        }
    }

}
