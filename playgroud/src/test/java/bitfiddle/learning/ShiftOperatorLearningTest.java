package bitfiddle.learning;

import org.junit.jupiter.api.Test;

import static bitfiddle.MoreInts.toBinary;
import static bitfiddle.MoreInts.toByte;
import static org.junit.Assert.assertEquals;

public class ShiftOperatorLearningTest {
    @Test
    public void operator_single_bit() {

        byte value1 = toByte("1");
        assertEquals("10", toBinary(value1 << 1));
        assertEquals("0", toBinary(value1 >> 1));
    }


    @Test
    public void operator_multiple_bit() {

        byte value1 = toByte("11");
        assertEquals("110", toBinary(value1 << 1));
        assertEquals("1", toBinary(value1 >> 1));
    }


    @Test
    public void operator_multiple_bit_more_examples() {

        byte value1 = toByte("10110");
        assertEquals("101100", toBinary(value1 << 1));
        assertEquals("1011", toBinary(value1 >> 1));
    }
}
