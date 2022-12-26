package socket.netty.eventstore;

public class ClientInfo {
    public final String host;
    public final int port;

    public ClientInfo(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String key() {
        return String.format("rpc://%s:%s", host, port);
    }
}
