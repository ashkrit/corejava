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

    @ParameterizedTest(name = "Format {1} to {0}")
    @ArgumentsSource(PriceFormatProvider.class)
    @DisplayName("Format price to string")
    public void format_price_to_string(String expected, Price value) {
        assertEquals(expected, format(value));
    }


    private String format(Price price) {
        return String.format("$%,.2f", price.toDollar());
    }

    static class PriceFormatProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of("$5.99", Price.cents(599)),
                    Arguments.of("$0.20", Price.cents(20)),
                    Arguments.of("$9.99", Price.cents(999)),
                    Arguments.of("$0.00", Price.cents(0)),
                    Arguments.of("$1,000.99", Price.cents(100099)),
                    Arguments.of("$9,986,666.99", Price.cents(998666699))
            );
        }
    }
}
