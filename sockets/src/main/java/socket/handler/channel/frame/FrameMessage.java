package socket.handler.channel.frame;

import java.nio.ByteBuffer;

public class FrameMessage {
    private final StringBuilder buffer = new StringBuilder(100);
    private int messageLength = -1;
    private int bytesRead = 0;

    public void append(String message, int bytes) {
        buffer.append(message);
        bytesRead += bytes;
    }

    public boolean isCompleteMessage() {
        return bytesRead >= messageLength;
    }

    public boolean hasMessageLength() {
        return messageLength != -1;
    }

    public String message() {
        return buffer.toString();
    }

    public void reset() {
        buffer.setLength(0);
        bytesRead = 0;
        messageLength = -1;
    }

    public ByteBuffer asBuffer() {
        return ByteBuffer.wrap(buffer.toString().getBytes());
    }

    public void messageLength(int value) {
        this.messageLength = value;
    }
}
