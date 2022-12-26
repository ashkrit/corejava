package socket.netty.impl;

import com.google.gson.Gson;

public class MessageHeader {
    public final byte version;
    public final byte flag;
    public final byte format;
    public final int messageSize;


    public MessageHeader(byte version, byte flag, byte format, int messageSize) {
        this.version = version;
        this.flag = flag;
        this.format = format;
        this.messageSize = messageSize;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
