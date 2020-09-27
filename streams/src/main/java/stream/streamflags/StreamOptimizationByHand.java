package stream.streamflags;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import net.agkn.hll.HLL;

import java.nio.charset.Charset;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class StreamOptimizationByHand {

    public static void main(String[] args) {

        Collection<String> setValue = createSet("ZZ", "Z", "B", "A", "C", "D", "A");
        Collection<String> sortedValue = createSortedSet("ZZ", "Z", "B", "A", "C", "D", "A");
        Collection<String> listOfValue = createList("ZZ", "Z", "B", "A", "C", "D", "A");

        System.out.println(String.format("ListOf Value Collection Count %s , Values %s", listOfValue.stream().distinct().count(), listOfValue));
        System.out.println(String.format("Sorted Collection Count %s , Values %s", sortedValue.stream().distinct().count(), sortedValue));
        System.out.println(String.format("Set Count %s , Values %s", setValue.stream().distinct().count(), setValue));

        int value = distinctCount((SortedSet<String>) sortedValue);
        System.out.println(String.format("Hand Rolled Count %s , Values %s", value, sortedValue));

        long approxCount = approxCount(listOfValue);
        System.out.println(String.format("Approx Rolled Count %s , Values %s", approxCount, listOfValue));


    }

    private static Collection<String> createSet(String value, String... args) {
        Set<String> container = new HashSet<>();
        container.add(value);
        for (String arg : args) {
            container.add(arg);
        }
        return container;
    }

    private static Collection<String> createSortedSet(String value, String... args) {
        SortedSet<String> container = new TreeSet<>();
        container.add(value);
        for (String arg : args) {
            container.add(arg);
        }
        return container;
    }

    private static Collection<String> createList(String value, String... args) {
        List<String> container = new ArrayList<>();
        container.add(value);
        for (String arg : args) {
            container.add(arg);
        }
        return container;
    }

    static int distinctCount(SortedSet<String> values) {
        Iterator<String> itr = values.iterator();
        if (!itr.hasNext()) return 0;

        String previous = itr.next();
        int itemCount = 1;
        while (itr.hasNext()) {
            String next = itr.next();
            if (!previous.equals(next)) {
                itemCount++;
                previous = next;
            }
        }
        return itemCount;
    }

    static long approxCount(Collection<String> values) {

        HashFunction hash = Hashing.murmur3_128();
        Supplier<HLL> supplier = () -> new HLL(8, 5);
        BiConsumer<HLL, String> consumer = (container, value) -> container.addRaw(toHash(hash, value));
        BinaryOperator<HLL> merge = (v1, v2) -> {
            v1.union(v2);
            return v1;
        };

        Collector<String, HLL, HLL> col = Collector.of(supplier, consumer, merge);

        return values.stream()
                .collect(col)
                .cardinality();
    }

    private static long toHash(HashFunction hash, String value) {
        return hash
                .newHasher()
                .putString(value, Charset.defaultCharset())
                .hash()
                .asLong();
    }
}
