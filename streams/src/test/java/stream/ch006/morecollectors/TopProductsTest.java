package stream.ch006.morecollectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import stream.collectors.TopXCollector;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DisplayName("Advance collectors")
public class TopProductsTest {

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
