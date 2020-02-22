package socket.handler.jetty;

public interface RequestProcessor<I, O> {
    O process(I input);

    Class<I> inputType();
}
