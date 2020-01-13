package tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddFractionsTest {

    @Test
    public void zeroPlusZero() {

        Fraction value = new Fraction(0).plus(new Fraction(0));
        assertEquals(0, value.intValue());
    }


    @Test
    public void nonZeroPlusZero() {
        Fraction value = new Fraction(5).plus(new Fraction(0));
        assertEquals(5, value.intValue());
    }

    @Test
    public void zeroPlusNonZero() {

        Fraction value = new Fraction(0).plus(new Fraction(10));
        assertEquals(10, value.intValue());
    }

    @Test
    public void nonZeroPositiveNumbers() {
        Fraction value = new Fraction(3).plus(new Fraction(2));
        assertEquals(5, value.intValue());
    }

    @Test
    public void negativePlusPositive() {
        Fraction value = new Fraction(1).plus(new Fraction(-6));
        assertEquals(-5, value.intValue());
    }

    @Test
    public void sameDenominatorPlusDifferentNominator() {
        Fraction value = new Fraction(1, 5).plus(new Fraction(2, 5));
        assertEquals(3, value.nominator());
        assertEquals(5, value.denominator());
    }
}
