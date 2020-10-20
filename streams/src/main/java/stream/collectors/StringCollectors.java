package stream.collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StringCollectors {
    public static Collector<String, StringBuilder, String> customStringJoiner(String delimiter) {

        Supplier<StringBuilder> supplier = () -> new StringBuilder(10);

        BiConsumer<StringBuilder, String> accumulator = (buffer, value) -> {
            if (buffer.length() > 0) {
                buffer.append(delimiter);
            }
            buffer.append(value);
        };

        BinaryOperator<StringBuilder> combiner = (buffer1, buffer2) -> {
            //Merge in bigger buffer to avoid GC
            if (buffer1.length() > buffer2.length()) {
                buffer1.append(buffer2);
                return buffer1;
            } else {
                buffer2.append(buffer1);
                return buffer2;
            }
        };

        Function<StringBuilder, String> finisher = v -> v.toString();

        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    public static Collector<String, List<String>, String> customStringJoinerUsingList(String delimiter) {

        Supplier<List<String>> supplier = () -> new ArrayList<>(5);

        BiConsumer<List<String>, String> accumulator = (buffer, value) -> {
            buffer.add(value);
        };

        BinaryOperator<List<String>> combiner = (buffer1, buffer2) -> {
            //Merge in bigger buffer to avoid GC
            if (buffer1.size() > buffer2.size()) {
                buffer1.addAll(buffer2);
                return buffer1;
            } else {
                buffer2.addAll(buffer1);
                return buffer2;
            }
        };

        Function<List<String>, String> finisher = v -> {
            StringBuilder sb = new StringBuilder(10);
            for (String element : v) {
                if (sb.length() > 0) {
                    sb.append(delimiter);
                }
                sb.append(element);
            }
            return sb.toString();
        };

        return Collector.of(supplier, accumulator, combiner, finisher);
    }
}
