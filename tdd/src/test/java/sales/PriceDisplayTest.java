package sales;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PriceDisplayTest {

    @ParameterizedTest(name = "Format {0} to {1}")
    @ArgumentsSource(PriceFormatProvider.class)
    @DisplayName("Format price to string")
    public void format_price_to_string(int price, String expected) {
        assertEquals(expected, format(price));
    }

    private String format(int value) {
        return String.format("$%,.2f", value / 100d);
    }

    static class PriceFormatProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(599, "$5.99"),
                    Arguments.of(20, "$0.20"),
                    Arguments.of(999, "$9.99"),
                    Arguments.of(0, "$0.00"),
                    Arguments.of(100099, "$1,000.99"),
                    Arguments.of(998666699, "$9,986,666.99")
            );
        }
    }
}
