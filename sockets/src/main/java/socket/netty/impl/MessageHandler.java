package socket.netty.impl;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class MessageHandler {

    public static ByteBuf writeHeader(int version, byte flag, byte format, int messageSize) {
        ByteBuf header = Unpooled.buffer(NetworkPacketMetaData.headerLength());
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

}
