package stream.bookselfs.custom;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static stream.bookselfs.custom.ZipCodeAllocationTest.Store.building;

public class ZipCodeAllocationTest {

    static class Store {
        public final int position;
        public final String name;

        Store(String name, int position) {
            this.position = position;
            this.name = name;
        }

        static Store building(String name, int position) {
            return new Store(name, position);
        }

        public int distance(Store lastStore) {
            return Math.abs(position - lastStore.position);
        }

        @Override
        public String toString() {
            return name + "(" + position + ")";
        }
    }

    @Test
    public void generatePostalCode() {

        /*
         Rule
          Building that are closed are part of same group and 2 buildings can be treated as close if distance between them is under 2 KM
         */

        List<Store> stores = Arrays.asList(
                building("S0", 67),
                building("S1", 100),
                building("S2", 101),
                building("S3", 107),
                building("S4", 108),
                building("S5", 114),
                building("S6", 116),
                building("S7", 117));


        Stream<Store> sortedByDistance = stores.stream().sorted(Comparator.comparingLong(x -> x.position));

        List<List<String>> q =
                sortedByDistance.collect(Collector.of(supplier, accumulator, combiner))
                        .stream()
                        .map(x -> x.stream().map(b -> b.name).collect(Collectors.toList()))
                        .collect(Collectors.toList());

        int index = 0;
        assertIterableEquals(Arrays.asList("S0"), q.get(index++));
        assertIterableEquals(Arrays.asList("S1", "S2"), q.get(index++));
        assertIterableEquals(Arrays.asList("S3", "S4"), q.get(index++));
        assertIterableEquals(Arrays.asList("S5", "S6", "S7"), q.get(index++));
    }

    Supplier<List<List<Store>>> supplier = () -> {
        List<List<Store>> initial = new ArrayList<>();
        initial.add(new ArrayList<>());
        return initial;
    };

    BiConsumer<List<List<Store>>, Store> accumulator = (buffer, value) -> {
        int total = buffer.size();
        List<Store> lastGroup = buffer.get(total - 1);

        if (!lastGroup.isEmpty()) {
            Store lastStore = lastGroup.get(lastGroup.size() - 1);
            if (value.distance(lastStore) > 2) {
                List<Store> newGroup = new ArrayList<>();
                newGroup.add(value);
                buffer.add(newGroup);
                return;
            }
        }
        lastGroup.add(value);
    };

    BinaryOperator<List<List<Store>>> combiner = (first, second) -> {

        if (first.isEmpty()) return second;
        if (second.isEmpty()) return first;

        List<Store> secondGroup = second.get(0);
        Store secondGroupFirst = secondGroup.get(0);

        List<Store> firstGroup = first.get(first.size() - 1);
        Store firstGroupLast = firstGroup.get(firstGroup.size() - 1);

        if (firstGroupLast.distance(secondGroupFirst) <= 2) {
            firstGroup.addAll(secondGroup);
            second.remove(secondGroup);
        }

        first.addAll(second);
        return first;
    };
}
