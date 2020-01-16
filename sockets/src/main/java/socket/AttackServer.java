package socket;

import java.io.IOException;
import java.net.Socket;

public class AttackServer {

    public static void main(String[] args) throws IOException, InterruptedException {
        var noOfConnection = 65_000;
        var clientConnections = new Socket[noOfConnection];
        for (int x = 0; x < noOfConnection; x++) {
            clientConnections[x] = new Socket("localhost", 8080);
        }

        Thread.sleep(100_000);
    }
}
