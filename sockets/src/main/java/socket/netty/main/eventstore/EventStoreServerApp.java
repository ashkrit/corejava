package socket.netty.main.eventstore;

import socket.netty.eventstore.EventStoreServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EventStoreServerApp {

    public static void main(String[] args) {

        int port = Integer.parseInt(args[0]);
        EventStoreServer server = new EventStoreServer(port);
        server.start();

        System.out.println("Type close to disconnect");

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        reader.lines().filter(li -> !li.isEmpty()).forEach(cmd -> {
            if (cmd.equalsIgnoreCase("stop")) {
                server.stop();
            } else {
                System.out.println("Not supported command " + cmd);
            }
        });
    }
}
