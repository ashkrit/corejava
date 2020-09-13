package stream.bookselfs.custom;

import org.junit.jupiter.api.Test;
import stream.collectors.TopXCollector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static stream.collectors.StringCollectors.customStringJoiner;
import static stream.collectors.StringCollectors.customStringJoinerUsingList;

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

    @Test
    public void topXProducts() {

        int TOP_MEMBER = 3;

        List<String> values = Arrays.asList("A", "B", "C", "A", "B", "D", "E", "F", "Q", "Q", "Q");

        Map<String, Long> frequency = toFrequency(values);

        List<String> expected = frequency.entrySet()
                .stream()
                .sorted(Comparator.comparingLong(x -> ((Map.Entry<String, Long>) x).getValue()).reversed())
                .limit(TOP_MEMBER)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> actual = values
                .stream()
                .collect(TopXCollector.top(TOP_MEMBER));

        expected.sort(Comparator.naturalOrder());
        actual.sort(Comparator.naturalOrder());

        assertIterableEquals(expected, actual);

    }

    private Map<String, Long> toFrequency(List<String> values) {
        return values
                .stream()
                .collect(Collectors.toMap(k -> k, k -> 1L, (v1, v2) -> v1 + v2));
    }


}
