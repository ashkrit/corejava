package socket;

public class MessageTransformer {

    public static int magic(int b) {
        return Character.isLetter(b) ? b ^ ' ' : b;
    }
}
