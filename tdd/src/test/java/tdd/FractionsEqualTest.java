package tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FractionsEqualTest {

    @Test
    public void sameNumeratorDenominator() {
        assertEquals(new Fraction(5, 2), new Fraction(5, 2));
    }


    @Test
    public void differentNumeratorDenominator() {
        assertNotEquals(new Fraction(6, 2), new Fraction(7, 3));
    }


    @Test
    public void differentNumeratorSameDenominator() {
        assertNotEquals(new Fraction(3, 5), new Fraction(4, 5));
    }


    @Test
    public void sameNumeratorDifferentDenominator() {
        assertNotEquals(new Fraction(3, 5), new Fraction(3, 6));
    }

    @Test
    public void wholeToDefaultFraction() {
        assertEquals(new Fraction(6), new Fraction(6, 1));
    }


    @Test
    public void wholeToNonFraction() {
        assertNotEquals(new Fraction(6), new Fraction(6, 2));
    }


}
