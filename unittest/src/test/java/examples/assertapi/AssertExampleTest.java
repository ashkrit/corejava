package examples.assertapi;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("assert api improvement")
public class AssertExampleTest {


    @Test
    @DisplayName("Error message using lambda supplier")
    public void lazy_error_message() {

        assertEquals(10, 10, () -> "This is wrong");

    }


    @Test
    @DisplayName("Group assertion")
    public void group_assertions() {

        List<String> stockMarkets = Arrays.asList("SGX", "BSE", "NSE", "NY");
        Assertions.assertAll("Stock Market",
                () -> assertEquals(stockMarkets.get(0), "SGX"),
                () -> assertEquals(stockMarkets.get(1), "BSE"),
                () -> assertEquals(stockMarkets.get(2), "NSE")
        );

    }


}
