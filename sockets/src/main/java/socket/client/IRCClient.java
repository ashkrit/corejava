package socket.client;

import java.io.*;
import java.net.Socket;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Map.entry;
import static java.util.Map.ofEntries;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static socket.client.IRCMessageCode.*;
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


        newSingleThreadExecutor().submit(task);

        var consoleStream = new BufferedReader(new InputStreamReader(System.in));
        consoleStream
                .lines()
                .forEach(line -> sendMessage(message, line));

    }

    private static void sendMessage(IRCMessageContext message, String line) {
        write(message.out, String.format("PRIVMSG %s :%s", message.channel, line));
    }

    private static IRCMessageHandler buildMessageHandlers(IRCMessageContext message, BufferedWriter out) {

        Map<Predicate<String>, BiConsumer<IRCMessageContext, Matcher>> handlers = ofEntries(
                entry(NICK_NAME_USED.code()::equalsIgnoreCase, nickNameTaken()),
                entry(WELCOME_END.code()::equalsIgnoreCase, joinChannel(message, out)),
                entry(CHANNEL_JOINED.code()::equalsIgnoreCase, (m, matcher) -> channelJoined(out, m, matcher)),
                entry(PRIVATE_MESSAGE.code()::equalsIgnoreCase, showPrivateMessage()));


        return new IRCMessageHandler(handlers);
    }

    private static BiConsumer<IRCMessageContext, Matcher> showPrivateMessage() {
        return (m, matcher) -> System.out.println(PRIVATE_MESSAGE.nextMessage(m, matcher));
    }

    private static void channelJoined(BufferedWriter out, IRCMessageContext m, Matcher matcher) {
        System.out.println(String.format("Joined %s channel", m.channel));
        write(out, CHANNEL_JOINED.nextMessage(m, matcher));
    }

    private static BiConsumer<IRCMessageContext, Matcher> joinChannel(IRCMessageContext message, BufferedWriter out) {
        return (m, matcher) -> write(out, IRCMessageCode.WELCOME_END.nextMessage(message, matcher));
    }

    private static BiConsumer<IRCMessageContext, Matcher> nickNameTaken() {
        return (m, matcher) -> System.out.println(NICK_NAME_USED.nextMessage(m, matcher));
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
