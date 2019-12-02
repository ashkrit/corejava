package math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FractionsReduceTest {

    @Test
    public void same_value_should_not_be_reduced() {
        assertEquals(new Fraction(4, 1), new Fraction(4, 1));
    }

    @Test
    public void gcd_examples() {
        assertEquals(1, gcd(1, 1));
        assertEquals(1, gcd(2, 1));
        assertEquals(1, gcd(3, 2));
        assertEquals(2, gcd(4, 2));
        assertEquals(6, gcd(54, 24));
        assertEquals(6, gcd(24, 54));
    }

    private int gcd(int a, int b) {
        while (b != 0) {
            int t = b;
            b = a % t;
            a = t;
        }
        return a;
    }
}
