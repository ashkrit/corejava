package stream.ch006.morecollectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static stream.collectors.StringCollectors.customStringJoiner;
import static stream.collectors.StringCollectors.customStringJoinerUsingList;

@DisplayName("Simple collector")
public class CustomCollectorTest {

    @Test
    public void combine_string_values() {

        List<String> values = Arrays.asList("A", "B", "C");

        String delimiter = ",";

        String expected = values.stream().collect(joining(delimiter));

        String actual = values.stream().collect(customStringJoiner(delimiter));
        String anotherActual = values.stream().collect(customStringJoinerUsingList(delimiter));

        assertEquals(expected, actual);
        assertEquals(expected, anotherActual);

    }

}
