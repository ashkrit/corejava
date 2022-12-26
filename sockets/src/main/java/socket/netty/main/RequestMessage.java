package socket.netty.main;

public class RequestMessage {
    public final String action;
    public final String messageId;
    public final ClientInfo clientInfo;

    public final Object message;

    public RequestMessage(ClientInfo clientInfo, String messageId, String action, Object message) {
        this.action = action;
        this.messageId = messageId;
        this.clientInfo = clientInfo;
        this.message = message;
    }
}
