package bitfiddle;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MoreInts {

    public static int toInt(String binaryValue) {
        return Integer.parseInt(binaryValue, 2);
    }

    public static byte toByte(String binaryValue) {
        return (byte) Integer.parseInt(binaryValue, 2);
    }

    public static String toBinary(int value) {
        return Integer.toBinaryString(value);
    }

    public static String toBinary(byte value) {
        String s = Integer.toBinaryString(value);
        int pad = 8 - s.length();
        return s.length() > 7 ? s.substring(s.length() - 8) : lPad(s, pad);

    }

    private static String lPad(String s, int pad) {
        return IntStream.range(0, pad)
                .mapToObj(x -> "0")
                .collect(Collectors.joining()) + s;
    }

}
