package bitfiddle.apps;

import bitfiddle.Bits;

/*
    Used for verification for data transferred over network or data saved on disk. Parity bits is used in many hardware for deducting errors.
    Caution: This is simple technique and comes with some limitation of deduction of error with odd or even.
    Hadoop name nodes performs some checks like this to check data integrity.
 */
public class Transmission {

    public static byte transmit(byte data) {
        return Bits.oddParity(data);
    }

    public static boolean verify(byte data) {
        return (Bits.countBits(data) & 1) == 1;
    }

}
