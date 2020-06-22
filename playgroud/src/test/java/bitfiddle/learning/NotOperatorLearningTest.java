package bitfiddle.learning;

import org.junit.jupiter.api.Test;

import static bitfiddle.MoreInts.toBinary;
import static bitfiddle.MoreInts.toByte;
import static org.junit.Assert.assertEquals;

public class NotOperatorLearningTest {
    @Test
    public void operator_single_bit() {

        byte value1 = (byte) (~toByte("1"));
        assertEquals("11111110", toBinary(value1));
    }


    @Test
    public void operator_multiple_bit() {

        byte value1 = (byte) ~toByte("11");
        assertEquals("11111100", toBinary(value1));
    }


    @Test
    public void operator_multiple_bit_more_examples() {

        byte value1 = (byte) ~toByte("10110");
        assertEquals("11101001", toBinary(value1));
    }
}
