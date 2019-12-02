package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class FractionsEqualsTest {

    @Test
    public void same_fraction_should_be_equals() {
        assertEquals(new Fraction(5, 10), new Fraction(5, 10));
    }


    @Test
    public void different_fraction_should_not_be_equals() {
        assertNotEquals(new Fraction(5, 10), new Fraction(6, 10));
    }

    @Test
    public void single_value_fraction_should_be_equal() {
        assertEquals(new Fraction(5), new Fraction(5, 1));
    }
}
