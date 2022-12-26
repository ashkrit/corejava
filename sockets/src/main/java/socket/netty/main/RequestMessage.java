package socket.netty.main;

public class RequestMessage {
    public final String action;
    public final String messageId;

    public final Object message;

    RequestMessage(String action, String messageId, Object message) {
        this.action = action;
        this.messageId = messageId;
        this.message = message;
    }
}
