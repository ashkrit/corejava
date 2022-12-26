package socket.netty.rpc.impl.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import socket.netty.rpc.impl.NetworkPacketMetaData;

import java.util.List;

public class HeaderMessageDecoder extends ByteToMessageDecoder {
    int readIndex = 0;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> list) {

        readIndex = in.readerIndex();
        if (in.readableBytes() < NetworkPacketMetaData.frameLengthInByte()) {
            return;
        }
        int frameSize = in.readInt();
        if (in.readableBytes() < frameSize) {
            return;
        }

        list.add(in.readBytes(frameSize));

    }
}
