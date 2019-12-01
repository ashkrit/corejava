package math;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FractionsAddTest {


    @Test
    public void adds_zero_value() {

        Fraction result = new Fraction(0).plus(new Fraction(0));
        Assertions.assertEquals(0, result.intValue());
    }
}
