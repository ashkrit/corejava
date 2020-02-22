package socket.handler;

import java.io.IOException;
import java.io.UncheckedIOException;

public class IOExceptionHandler<S> implements ClientHandler<S> {

    private final ClientHandler<S> handler;

    public IOExceptionHandler(ClientHandler<S> handler) {
        this.handler = handler;
    }

    @Override
    public void handle(S s) {
        try {
            handler.handle(s);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
