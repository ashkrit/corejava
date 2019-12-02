package math;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;

import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.of;

public class FractionVariationsTest {

    @ParameterizedTest
    @ArgumentsSource(FractionsArgumentProvider.class)
    public void checkFractionValues(Fraction expected, List<Fraction> input) {

        Fraction actualValue = input.get(0);
        for (Fraction fraction : input.subList(1, input.size())) {
            actualValue = actualValue.plus(fraction);
        }

        assertEquals(expected, actualValue);
    }


    static class FractionsArgumentProvider implements ArgumentsProvider {

        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(
                    of(new Fraction(0), asList(new Fraction(0), new Fraction(0))),
                    of(new Fraction(1), asList(new Fraction(1, 3),
                            new Fraction(2, 3))),
                    of(new Fraction(5, 6), asList(new Fraction(1, 2),
                            new Fraction(1, 3)))
            );
        }
    }
}
