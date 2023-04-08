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

public class Experiment<T> {
    private final String name;
    private Supplier<T> control;
    private Supplier<T> candidate;

    private final ConcurrentMap<Long, ExperimentResult> results = new ConcurrentSkipListMap<>();
    private final List<BiFunction<T, T, Object>> resultComparator = new CopyOnWriteArrayList<>();
    private List<Object> compareResults;
    private final AtomicLong sequence = new AtomicLong();

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

    public void run() {
        long seq = sequence.incrementAndGet();
        results.put(seq, new ExperimentResult<>(control.get(), candidate.get()));

        ExperimentResult<T> result = results.get(seq);

        compareResults = resultComparator.stream()
                .map(c -> c.apply(result.control, result.candidate))
                .collect(Collectors.toList());

    }


    public Experiment<T> compareResult(BiFunction<T, T, Object> compare) {
        resultComparator.add(compare);
        return this;
    }

    public void publish(Consumer<Object> consumer) {
        compareResults.forEach(consumer::accept);
    }

    public void publish() {
        publish(System.out::println);
    }
}
