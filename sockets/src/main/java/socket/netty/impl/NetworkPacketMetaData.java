package socket.netty.impl;

/**
 * FORMAT.....
 * FRAME LENGTH
 * VERSION FLAGS FORMAT MESSAGE_SIZE
 * MESSAGE
 *
 *
 * Eg
 * FRAME LENGTH - Int ( 32)
 * Version - Byte ( 8)
 * Flag - Byte (8)
 * FORMAT - Byte ( 8)
 * MESSAGE_SIZE - Int ( 32)
 * Message - based on Message Size
 *
 *
 * 8 + 8 + 8 + 32 =
 *
 */

public class NetworkPacketMetaData {

    static final int VERSION = 8;
    static final int FLAG = 8;

    static final int FORMAT = 8;

    static final int MESSAGE_SIZE = 8 * 4;

    public static int headerLength() {
        return VERSION + FLAG + FORMAT + MESSAGE_SIZE;
    }

    public static int frameLength() {
        return 8 * 4;
    }

    public static int frameLengthInByte() {
        return frameLength() / 8;
    }


}
