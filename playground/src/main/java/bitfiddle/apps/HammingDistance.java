package bitfiddle.apps;

import bitfiddle.Bits;

/*
    Using bits count to find distance between 2 integer. Some of application are error deduction while data transfer
 */
public class HammingDistance {

    public static int weight(int value) {
        return Bits.countBits(value);
    }

    public static int distance(int value1, int value2) {
        return Bits.countBits(value1 ^ value2);
    }

    public static int distance(String value1, String value2) {
        throw new IllegalArgumentException("Not implemented");
    }
}
