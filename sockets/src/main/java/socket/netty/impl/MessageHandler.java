package socket.netty.impl;

import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageHandler {

    public static ByteBuf createHeader(int version, byte flag, byte format, int messageSize) {
        ByteBuf header = Unpooled.buffer(MessagePacket.headerLength());
        header.writeByte(version);
        header.writeByte(flag);
        header.writeByte(format);
        header.writeInt(messageSize);
        return header;
    }

    public static MessageHeader readHeader(ByteBuf in) {
        byte version = in.readByte();
        byte flag = in.readByte();
        byte format = in.readByte();
        int messageSize = in.readInt();
        return new MessageHeader(version, flag, format, messageSize);
    }

    public static int calculateFrameSize(ByteBuf header, ByteBuf messageBody) {
        return header.readableBytes() + messageBody.readableBytes();
    }

    public static class MessageHeader {
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

}
