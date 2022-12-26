package socket.netty.main;

public class Client {
    public final String host;
    public final int port;

    Client(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String key() {
        return String.format("rpc://%s:%s", host, port);
    }
}
