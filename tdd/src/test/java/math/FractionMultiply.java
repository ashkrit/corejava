package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionMultiply {

    @Test
    public void multiply_whole_number() {
        assertEquals(new Fraction(1), new Fraction(1).multiply(new Fraction(1)));
    }
}
