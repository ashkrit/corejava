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

    @ParameterizedTest(name = "Format {2} to {1}")
    @ArgumentsSource(PriceFormatProvider.class)
    @DisplayName("Format price to string")
    public void format_price_to_string(int price, String expected, Price value) {
        assertEquals(expected, format(toDollar(price), value));
    }

    private double toDollar(int price) {
        return price / 100d;
    }

    private String format(double value, Price price) {
        return String.format("$%,.2f", price.toDollar());
    }

    static class PriceFormatProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    Arguments.of(599, "$5.99", Price.cents(599)),
                    Arguments.of(20, "$0.20", Price.cents(20)),
                    Arguments.of(999, "$9.99", Price.cents(999)),
                    Arguments.of(0, "$0.00", Price.cents(0)),
                    Arguments.of(100099, "$1,000.99", Price.cents(100099)),
                    Arguments.of(998666699, "$9,986,666.99", Price.cents(998666699))
            );
        }
    }
}
