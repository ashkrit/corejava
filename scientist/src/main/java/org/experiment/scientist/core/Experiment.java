package org.experiment.scientist.core;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Experiment<T> {
    private final String name;
    private Supplier<T> control;
    private Supplier<T> candidate;

    private final List<BiFunction<T, T, Object>> resultComparator = new CopyOnWriteArrayList<>();
    private ConcurrentMap<Long, List<Object>> compareResults = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private int times = 1;
    private boolean parallel;

    public Experiment(String name) {
        this.name = name;
    }

    public Experiment<T> withControl(Supplier<T> control) {
        this.control = control;
        return this;
    }

    public Experiment<T> withCandidate(Supplier<T> candidate) {
        this.candidate = candidate;
        return this;
    }

    public Experiment<T> run() {

        compareResults.clear();


        IntStream stream = IntStream.range(0, times);

        if (parallel) {
            stream = stream.parallel();
        }

        stream.forEach($ -> _execute());

        return this;

    }

    private void _execute() {
        ExperimentResult<T> result = new ExperimentResult<>(control.get(), candidate.get());
        long nextSeq = sequence.incrementAndGet();
        List<Object> results = resultComparator.stream().map(c -> c.apply(result.control, result.candidate)).collect(Collectors.toList());

        compareResults.put(nextSeq, results);
    }


    public Experiment<T> compareResult(BiFunction<T, T, Object> compare) {
        resultComparator.add(compare);
        return this;
    }

    public Experiment<T> publish(Consumer<Object> consumer) {
        consumer.accept(name);
        compareResults.values().forEach(consumer);
        return this;
    }

    public Experiment<T> publish() {
        publish(System.out::println);
        return this;
    }

    public Experiment<T> times(int times) {
        this.times = times;
        return this;
    }

    public Experiment<T> parallel() {
        this.parallel = true;
        return this;
    }
}
