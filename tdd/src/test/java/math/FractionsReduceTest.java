package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionsReduceTest extends GreaterCommonDivisor {

    @Test
    public void same_value_should_not_be_reduced() {
        assertEquals(new Fraction(4, 1), new Fraction(4, 1));
    }

    @Test
    public void value_should_be_reduced_by_gcd() {
        assertEquals(new Fraction(6, 4), new Fraction(3, 2));
    }

}
