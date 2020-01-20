package socket.server.jetty;

import org.eclipse.jetty.server.Server;
import socket.handler.jetty.GenericRequestHandler;
import socket.handler.jetty.StockPriceProcessor;

public class JettyServer {

    public static void main(String[] args) throws Exception {
        Server server = new Server(8080);
        server.setHandler(new GenericRequestHandler() {{
            map("/stock", new StockPriceProcessor());
        }});
        server.start();
        server.join();
    }
}
