package socket.netty.main;

import com.google.gson.Gson;
import socket.netty.MessageClient;
import socket.netty.MessageFormat;
import socket.netty.impl.MessageQueueClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.UUID;

public class MessageClientApp {

    public static void main(String[] args) {

        String host = args[0];
        int port = Integer.parseInt(args[1]);
        MessageClient client = new MessageQueueClient(host, port);

        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        reader.lines().filter(l -> !l.isEmpty()).forEach(line -> {


            client.send(line.getBytes(), MessageFormat.String);

            RequestMessage message = new RequestMessage("/ping", UUID.randomUUID().toString(), line);
            client.send(new Gson().toJson(message).getBytes(), MessageFormat.Json);
        });

    }

    public static class RequestMessage {
        public final String action;
        public final String messageId;

        public final Object message;

        RequestMessage(String action, String messageId, Object message) {
            this.action = action;
            this.messageId = messageId;
            this.message = message;
        }
    }


}
