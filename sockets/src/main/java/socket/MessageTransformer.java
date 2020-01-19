package socket;

import java.nio.ByteBuffer;

public class MessageTransformer {

    public static int magic(int b) {
        return Character.isLetter(b) ? b ^ ' ' : b;
    }

    public static void magic(ByteBuffer buffer) {
        System.out.println("Processing done by " + Thread.currentThread());
        buffer.flip(); // Switch to read mode
        for (int i = 0; i < buffer.limit(); i++) {
            buffer.put(i, (byte) magic(buffer.get(i)));
        }
    }
}
