package socket.netty.main.eventstore;

import socket.netty.eventstore.EventStore;
import socket.netty.eventstore.impl.RemoteEventStore;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class EventStoreClientApp {

    public static void main(String[] args) {
        EventStore<String> es = new RemoteEventStore<>("localhost", 8080, String.class);
        es.registerConsumer("test", m -> {
            System.out.println("Consume message :" + m);
        });

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        reader.lines().filter(l -> !l.isEmpty()).forEach(line -> {
            es.publish(line);
        });

    }
}
