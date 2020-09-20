package stream.collectors;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class TopXCollector {

    public static <K> Collector<K, Summary<K>, List<K>> top(int top) {

        Supplier<Summary<K>> supplier = () -> new Summary<>();

        BiConsumer<Summary<K>, K> accumulator = (buffer, value) -> {
            buffer.frequency.compute(value, (k, v) -> v == null ? 1 : v + 1);
        };

        BinaryOperator<Summary<K>> combiner = (buffer1, buffer2) -> {
            if (isGt(buffer1.frequency, buffer2.frequency)) {
                buffer1.merge(buffer2);
                return buffer1;
            } else {
                buffer2.merge(buffer1);
                return buffer2;
            }
        };

        Function<Summary<K>, List<K>> finisher = v -> {
            Map.Entry<K, Long>[] t = collectElement(top, v);
            return Arrays.asList(t)
                    .stream()
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());
        };

        return Collector.of(supplier, accumulator, combiner, finisher);
    }

    private static <K> Map.Entry<K, Long>[] collectElement(int top, Summary<K> v) {

        /*
         This logic can be also implemented by BTree or PriorityQueue
         */

        int cnt = 0;
        Comparator<Map.Entry<K, Long>> sortByFrequency = Comparator.comparingLong(x -> x.getValue());

        Map.Entry<K, Long>[] t = (Map.Entry<K, Long>[]) new Map.Entry[top];
        for (Map.Entry<K, Long> e : v.frequency.entrySet()) {
            if (cnt < top) {
                t[cnt++] = e;
                if (cnt == top) {
                    Arrays.sort(t, sortByFrequency);
                }
            } else {
                if (isLessThan(t[0], e)) {
                    t[0] = e;
                    Arrays.sort(t, sortByFrequency);
                }
            }
        }
        return t;
    }

    private static <K> boolean isLessThan(Map.Entry<K, Long> left, Map.Entry<K, Long> right) {
        return left.getValue() < right.getValue();
    }

    private static <K> boolean isGt(Map<K, Long> collection1, Map<K, Long> collection2) {
        return collection1.size() > collection2.size();
    }

    public static class Summary<K> {
        Map<K, Long> frequency = new HashMap<>();

        void merge(Summary<K> s) {
            s.frequency.forEach((e, newValueToMerge) -> frequency.merge(e, newValueToMerge, Long::sum));
        }
    }
}
