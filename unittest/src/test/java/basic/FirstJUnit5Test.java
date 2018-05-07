package basic;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FirstJUnit5Test {

    @Test
    void myFirstTest() {
        assertEquals(2, 1 + 1);
    }
}
