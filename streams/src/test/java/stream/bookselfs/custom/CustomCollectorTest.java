package stream.bookselfs.custom;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CustomCollectorTest {

    @Test
    public void combine_string_values() {

        List<String> values = Arrays.asList("A", "B", "C");

        String actual = values.stream().collect(customStringJoiner());
        String expected = values.stream().collect(Collectors.joining(","));

        assertEquals(expected, actual);
    }

    private Collector<String, StringBuffer, String> customStringJoiner() {

        Supplier<StringBuffer> supplier = () -> new StringBuffer();

        BiConsumer<StringBuffer, String> accumulator = (buffer, value) -> {
            if (buffer.length() > 0) {
                buffer.append(",");
            }
            buffer.append(value);
        };

        BinaryOperator<StringBuffer> combiner = (buffer1, buffer2) -> {
            //Merge in bigger buffer to avoid GC
            if (buffer1.length() > buffer2.length()) {
                buffer1.append(buffer2);
                return buffer1;
            } else {
                buffer2.append(buffer1);
                return buffer2;
            }
        };

        Function<StringBuffer, String> finisher = v -> v.toString();

        return Collector.of(supplier, accumulator, combiner, finisher);
    }
}
