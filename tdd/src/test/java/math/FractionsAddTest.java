package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionsAddTest {


    @Test
    public void adds_zero_value() {

        Fraction result = new Fraction(0).plus(new Fraction(0));
        assertEquals(new Fraction(0), result);
    }

    @Test
    public void add_single_zero_value() {
        Fraction result = new Fraction(2).plus(new Fraction(0));
        assertEquals(new Fraction(2), result);
    }


    @Test
    public void add_zero_and_non_zero_value() {
        Fraction result = new Fraction(0).plus(new Fraction(5));
        assertEquals(new Fraction(5), result);
    }

    @Test
    public void add_both_non_zero_value() {
        Fraction result = new Fraction(10).plus(new Fraction(5));
        assertEquals(new Fraction(15), result);
    }


    @Test
    public void add_negative_value_test() {
        Fraction result = new Fraction(-4).plus(new Fraction(2));
        assertEquals(new Fraction(-2), result);
    }

    @Test
    public void add_values_with_non_zero_denominator() {

        Fraction result = new Fraction(1, 5).plus(new Fraction(2, 5));
        assertEquals(new Fraction(3, 5), result);
    }


    @Test
    public void different_denominators() {
        Fraction result = new Fraction(1, 2).plus(new Fraction(1, 3));
        assertEquals(new Fraction(5, 6), result);
    }

}
