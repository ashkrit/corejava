package bitfiddle.learning;

import org.junit.jupiter.api.Test;

import static bitfiddle.MoreInts.toBinary;
import static bitfiddle.MoreInts.toInt;
import static org.junit.Assert.assertEquals;

public class XOROperatorLearningTest {
    @Test
    public void operator_single_bit() {

        int value1 = toInt("1");
        int value2 = toInt("0");
        assertEquals("1", toBinary(value1 ^ value2));
    }


    @Test
    public void operator_multiple_bit() {

        int value1 = toInt("11");
        int value2 = toInt("10");
        assertEquals("1", toBinary(value1 ^ value2));
    }


    @Test
    public void operator_multiple_bit_more_examples() {

        int value1 = toInt("10110");
        int value2 = toInt("101");
        assertEquals("10011", toBinary(value1 ^ value2));
    }
}
