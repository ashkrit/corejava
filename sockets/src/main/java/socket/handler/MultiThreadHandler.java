package socket.handler;

import java.io.IOException;
import java.io.UncheckedIOException;

public class MultiThreadHandler<S> implements ClientHandler<S> {

    private final ClientHandler<S> handler;

    public MultiThreadHandler(ClientHandler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(S s) {
        new Thread(() -> {
            try {
                handler.handle(s);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }).start();

    }
}
