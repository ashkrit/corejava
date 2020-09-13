package stream.collectors;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ZipCodeCollector {

    static Supplier<List<List<Store>>> supplier = () -> {
        List<List<Store>> initial = new ArrayList<>();
        initial.add(new ArrayList<>());
        return initial;
    };

    static BiConsumer<List<List<Store>>, Store> accumulator = (buffer, value) -> {
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

    static BinaryOperator<List<List<Store>>> combiner = (first, second) -> {

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


    public static Collector<Store, List<List<Store>>, List<List<Store>>> create() {Ad
        /*
         .....
                | Split
                    |    Supplier* | accumulator
                    |    Supplier | accumulator
                    |    Supplier | accumulator
                            | Combiner
                                    | Finisher

         */
        return Collector.of(supplier, accumulator, combiner);
    }

    public static class Store {
        public final int position;
        public final String name;

        Store(String name, int position) {
            this.position = position;
            this.name = name;
        }

        public static Store building(String name, int position) {
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
}
