package stream.collectors;

import stream.collectors.ZipCodeCollector.Store;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class ZipCodeCollectorV2 {


    static Supplier<Deque<Deque<Store>>> supplierQ = () -> {
        Deque<Deque<Store>> initial = new ArrayDeque<>();
        initial.add(new ArrayDeque<>());
        return initial;
    };


    static BiConsumer<Deque<Deque<Store>>, Store> accumulatorQ = (buffer, value) -> {
        int total = buffer.size();
        Deque<Store> lastGroup = buffer.getLast();

        if (!lastGroup.isEmpty()) {
            Store lastStore = lastGroup.getLast();
            if (value.distance(lastStore) > 2) {
                Deque<Store> newGroup = new ArrayDeque<>();
                newGroup.add(value);
                buffer.add(newGroup);
                return;
            }
        }
        lastGroup.add(value);
    };

    static BinaryOperator<Deque<Deque<Store>>> combinerQ = (first, second) -> {

        if (first.isEmpty()) return second;
        if (second.isEmpty()) return first;

        Deque<Store> secondGroup = second.getFirst();
        Store secondGroupFirst = secondGroup.getFirst();

        Deque<Store> firstGroup = first.getLast();
        Store firstGroupLast = firstGroup.getLast();

        if (firstGroupLast.distance(secondGroupFirst) <= 2) {
            firstGroup.addAll(secondGroup);
            second.removeFirst();
        }
        first.addAll(second);
        return first;
    };

    public static Collector<Store, Deque<Deque<Store>>, Deque<Deque<Store>>> create() {
        return Collector.of(supplierQ, accumulatorQ, combinerQ);
    }
}
