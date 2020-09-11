package stream.bookselfs.custom;

import org.junit.jupiter.api.Test;
import stream.collectors.StringCollectors;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomCollectorTest {

    @Test
    public void combine_string_values() {

        List<String> values = Arrays.asList("A", "B", "C");

        String delimiter = ",";

        String actual = values.stream().collect(StringCollectors.customStringJoiner(delimiter));
        String expected = values.stream().collect(Collectors.joining(delimiter));
        String anotherActual = values.stream()
                .collect(StringCollectors.customStringJoinerUsingList(delimiter));

        assertEquals(expected, actual);
        assertEquals(expected, anotherActual);

    }

}
