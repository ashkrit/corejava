package bitfiddle;

public class Bits {
    /*
    Integer.bitCount is much more efficient way of doing this but this example is used to simplicity.
    */
    public static int countBits(int value) {
        int count = 0;
        while (value != 0) {
            count += value & 1;
            value = value >>> 1;
        }
        return count;
    }

    public static byte evenParity(byte value) {
        int c = countBits(value);
        int v = (c & 1) == 0 ? value | 0 << 7 : value | 1 << 7;
        return (byte) v;
    }

    public static byte oddParity(byte value) {
        int c = countBits(value);
        int v = (c & 1) == 1 ? value | 0 << 7 : value | 1 << 7;
        return (byte) v;
    }

    public static int set(int value, int i) {
        return (value | (1 << i - 1));
    }

    public static int clear(int value, int i) {
        return (value & ~(1 << i - 1));
    }

    public static int toggle(int value, int i) {
        return (value ^ (1 << i - 1));
    }

    public static boolean isSet(int value, int i) {
        return (value & (1 << i - 1)) > 0;
    }
}
