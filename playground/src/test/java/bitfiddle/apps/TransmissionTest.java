package bitfiddle.apps;

import org.junit.jupiter.api.Test;

import static bitfiddle.Bits.oddParity;
import static bitfiddle.MoreInts.toByte;
import static bitfiddle.apps.Transmission.transmit;
import static bitfiddle.apps.Transmission.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TransmissionTest {

    @Test
    public void verify_good_transfer() {
        byte pData = transmit(oddParity(toByte("1001")));
        assertTrue(verify(pData));
    }


    @Test
    public void fail_bad_transfer() {
        byte pData = transmit(oddParity(toByte("1000")));

        pData = (byte) (pData | 1); // Add some error

        assertFalse(verify(pData));
    }
}
