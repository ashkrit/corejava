package socket.client;

import java.util.regex.Matcher;

public enum IRCMessageCode {

    NICK_NAME_USED("433" ) {
        public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
            return String.format("Nick name (%s) is already used", message.nick);
        }
    },
    LOGIN_SUCCESS("001" ),
    USER_DETAILS("251" ),
    WELCOME_MESSAGE("372" ),
    WELCOME_END("376" ) {
        public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
            return String.format("JOIN %s", message.channel);
        }
    },
    PING("PING" ) {
        public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
            return String.format("PONG %s", messageMatcher.group(2));
        }
    },
    CHANNEL_JOINED("366" ) {
        public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
            return String.format("PRIVMSG %s :%s", message.channel, "I am In" );
        }
    },
    PRIVATE_MESSAGE("PRIVMSG" ) {
        public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
            return String.format("[%s] %s", messageMatcher.group(1), messageMatcher.group(4));
        }
    };

    private final String code;

    IRCMessageCode(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    public String nextMessage(IRCClient.IRCMessageContext message, Matcher messageMatcher) {
        return messageMatcher.group(4);
    }
}
