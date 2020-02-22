package socket.server.thread;

import socket.handler.*;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceBlockingServer {

    public static void main(String... args) throws IOException {
        var server = new ServerSocket(8080);
        var handler = new ExecutorServiceHandler(new IOExceptionHandler(new PrintingHandler<>(new MagicHandler())),
                Executors.newCachedThreadPool(),
                (thread, e) -> System.out.println("Error " + thread + " Message " + e));
        while (true) {
            var clientSocket = server.accept();
            handler.handle(clientSocket);
        }
    }


}
