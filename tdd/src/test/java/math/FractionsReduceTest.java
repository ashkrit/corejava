package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionsReduceTest extends GreaterCommonDivisor {

    @Test
    public void same_value_should_not_be_reduced() {
        assertEquals(new Fraction(4, 1), new Fraction(4, 1));
    }

}
