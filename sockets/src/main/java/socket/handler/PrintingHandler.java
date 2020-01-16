package socket.handler;

import java.io.IOException;

public class PrintingHandler<S> implements ClientHandler<S> {

    private final ClientHandler<S> handler;

    public PrintingHandler(ClientHandler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(S s) throws IOException {
        System.out.println("Connected to " + s);
        try {
            handler.handle(s);
        } finally {
            System.out.println("Disconnected from " + s);
        }
    }
}
