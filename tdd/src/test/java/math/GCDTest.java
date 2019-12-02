package math;

import org.junit.jupiter.api.Test;

import static math.GreaterCommonDivisor.gcd;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class GCDTest {
    @Test
    public void gcd_examples() {
        assertEquals(1, gcd(1, 1));
        assertEquals(1, gcd(2, 1));
        assertEquals(1, gcd(3, 2));
        assertEquals(2, gcd(4, 2));
        assertEquals(6, gcd(54, 24));
        assertEquals(6, gcd(24, 54));
    }

}
