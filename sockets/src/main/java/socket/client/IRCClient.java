package socket.client;

import java.io.*;
import java.net.Socket;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static socket.client.IRCMessageCode.NICK_NAME_USED;
import static socket.client.IRCMessageCode.PRIVATE_MESSAGE;
import static socket.client.IRCMessageCode.WELCOME_END;
import static socket.functions.SafeFunctions.toUnChecked;

public class IRCClient {

    static Pattern commandMessagePattern = Pattern.compile("(:.*) (...|PRIVMSG) (.*) (:.*)" );
    static Pattern serverReply = Pattern.compile("(.*) (.*)" );

    static class IRCMessageContext {
        final String nick;
        final String user;
        final String channel;
        BufferedWriter out;

        private IRCMessageContext(String nick, String user, String channel) {
            this.nick = nick;
            this.user = user;
            this.channel = channel;
        }

        public void messageStream(BufferedWriter out) {
            this.out = out;
        }
    }

    public static void main(String[] args) throws Exception {

        String nick = args[0];
        String user = args[1];
        String channel = args[2];

        var message = new IRCMessageContext(nick, user, channel);

        var config = loadConfig("/server.conf" );
        System.out.println(config);

        var port = Integer.parseInt(config.getProperty("irc.port" ));
        var server = config.getProperty("irc.server" );


        Callable<Void> task = () -> {
            var handshakeDone = new AtomicBoolean(false);
            var socket = new Socket(server, port);
            try (var in = toBufferedReader(socket.getInputStream());
                 var out = toBufferedWriter(socket.getOutputStream())) {

                message.messageStream(out);
                var handler = buildMessageHandlers(message, out);
                in
                        .lines()
                        .forEach(action -> processAction(out, handshakeDone, action, message, handler));

            }
            return null;
        };


        Executors.newSingleThreadExecutor().submit(task);

        new BufferedReader(new InputStreamReader(System.in)).lines().forEach(line -> {
            write(message.out, String.format("PRIVMSG %s :%s", message.channel, line));
        });

    }

    private static IRCMessageHandler buildMessageHandlers(IRCMessageContext message, BufferedWriter out) {
        return new IRCMessageHandler(ofEntries(
                entry(NICK_NAME_USED.code()::equalsIgnoreCase,
                        (m, matcher) -> System.out.println(NICK_NAME_USED.nextMessage(m, matcher))),
                entry(WELCOME_END.code()::equalsIgnoreCase,
                        (m, matcher) -> write(out, IRCMessageCode.WELCOME_END.nextMessage(message, matcher))),
                entry(IRCMessageCode.CHANNEL_JOINED.code()::equalsIgnoreCase, (m, matcher) -> {
                    System.out.println(String.format("Joined %s channel", m.channel));
                    write(out, IRCMessageCode.CHANNEL_JOINED.nextMessage(m, matcher));
                }),
                entry(PRIVATE_MESSAGE.code()::equalsIgnoreCase,
                        (m, matcher) -> System.out.println(PRIVATE_MESSAGE.nextMessage(m, matcher))

                )));
    }

    private static void processAction(BufferedWriter out, AtomicBoolean handshakeDone,
                                      String action, IRCMessageContext message, IRCMessageHandler handlers) {
        try {


            //System.out.println(action);
            checkForPing(out, action, message);
            processServerReply(action, message, handlers);
            registerUserIfRequired(out, handshakeDone, message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void processServerReply(String action, IRCMessageContext message, IRCMessageHandler handlers) {
        var matcher = commandMessagePattern.matcher(action);
        if (matcher.matches()) {
            String command = matcher.group(2).trim();
            handlers.match(command).ifPresent(h -> h.accept(message, matcher));
        }
    }

    private static void checkForPing(BufferedWriter out, String action, IRCMessageContext message) {
        var serverReplyMatcher = serverReply.matcher(action);
        if (serverReplyMatcher.matches()) {
            String command = serverReplyMatcher.group(1);
            if (IRCMessageCode.PING.code().equalsIgnoreCase(command)) {
                write(out, IRCMessageCode.PING.nextMessage(message, serverReplyMatcher));
            }
        }
    }

    private static void registerUserIfRequired(BufferedWriter out, AtomicBoolean register, IRCMessageContext message) {
        if (!register.get()) {
            write(out, String.format("NICK %s", message.nick));
            write(out, String.format("USER %s 8 * : IRC Hacks Bot", message.user));
            register.set(true);
        }
    }

    private static void write(BufferedWriter out, String message) {
        try {
            System.out.println(message);
            out.write(message);
            out.newLine();
            out.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static BufferedReader toBufferedReader(InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream));
    }


    private static BufferedWriter toBufferedWriter(OutputStream outputStream) {
        return new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    private static Properties loadConfig(String configFile) throws IOException {
        try (var in = IRCClient.class.getResourceAsStream(configFile)) {

            var configStream = Optional.ofNullable(in);
            var prop = configStream
                    .map(toUnChecked(IRCClient::load))
                    .orElseThrow(() -> new IllegalArgumentException("Unable to load" ));
            return prop;
        }
    }

    private static Properties load(InputStream s) throws IOException {
        Properties prop = new Properties();
        prop.load(s);
        return prop;
    }
}
