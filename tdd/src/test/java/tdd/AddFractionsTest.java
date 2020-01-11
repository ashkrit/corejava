package tdd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddFractionsTest {

    @Test
    public void addZeroValue() {

        Fraction value = new Fraction(0).plus(new Fraction(0));
        assertEquals(0, value.intValue());
    }


    @Test
    public void nonZeroPlusZero() {
        Fraction value = new Fraction(5).plus(new Fraction(0));
        assertEquals(5, value.intValue());
    }
}
