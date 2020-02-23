package socket.client;

import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.regex.Matcher;

public class IRCMessageHandler {

    private Map<Predicate<String>, BiConsumer<IRCClient.IRCMessageContext, Matcher>> handlers;

    public IRCMessageHandler(Map<Predicate<String>, BiConsumer<IRCClient.IRCMessageContext, Matcher>> handlers) {
        this.handlers = handlers;
    }

    public Optional<BiConsumer<IRCClient.IRCMessageContext, Matcher>> match(String command) {
        return handlers.entrySet().stream().filter(entry -> entry.getKey().test(command))
                .map(entry -> entry.getValue())
                .findFirst();
    }


}
