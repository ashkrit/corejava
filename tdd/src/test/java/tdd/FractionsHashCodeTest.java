package tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FractionsHashCodeTest {

    @Test
    public void sameNumeratorDenominator() {

        assertEquals(new Fraction(5, 2).hashCode(), new Fraction(5, 2).hashCode());
    }


    @Test
    public void differentNumeratorDenominator() {

        assertNotEquals(new Fraction(6, 2).hashCode(), new Fraction(7, 3).hashCode());
    }


    @Test
    public void differentNumeratorSameDenominator() {

        assertNotEquals(new Fraction(3, 5).hashCode(), new Fraction(4, 5).hashCode());
    }
}
