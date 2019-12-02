package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionMultiply {

    @Test
    public void multiply_whole_number() {
        assertEquals(new Fraction(1), new Fraction(1).multiply(new Fraction(1)));
    }

    @Test
    public void multiply_whole_number_gt_1() {
        assertEquals(new Fraction(5), new Fraction(1).multiply(new Fraction(5)));
    }

    @Test
    public void multiply_fraction_values_with_same_denominator() {
        assertEquals(new Fraction(1, 5),
                new Fraction(1, 2).multiply(new Fraction(2, 5)));
    }

}
