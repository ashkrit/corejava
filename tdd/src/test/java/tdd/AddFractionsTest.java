package tdd;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class AddFractionsTest {

    @Test
    public void addZeroValue() {

        Fraction value = new Fraction(0).plus(new Fraction(0));
        Assertions.assertEquals(0,value.intValue());
    }
}
