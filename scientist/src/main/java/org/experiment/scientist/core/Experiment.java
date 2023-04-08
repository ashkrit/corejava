package org.experiment.scientist.core;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Supplier;

public class Experiment<T> {
    private final String name;
    private Supplier<T> test;
    private Supplier<T> control;

    private final ConcurrentMap<Long, ExperimentResult> results = new ConcurrentSkipListMap<>();
    private final List<BiFunction<T, T, Object>> comparators = new CopyOnWriteArrayList<>();
    private final AtomicLong sequence = new AtomicLong();

    public Experiment(String name) {
        this.name = name;
    }

    public Experiment<T> withTest(Supplier<T> test) {
        this.test = test;
        return this;
    }

    public Experiment<T> withControl(Supplier<T> control) {
        this.control = control;
        return this;
    }

    public void run() {
        long seq = sequence.incrementAndGet();
        results.put(seq, new ExperimentResult<>(test.get(), control.get()));

        ExperimentResult<T> result = results.get(seq);

        comparators
                .forEach(c -> c.apply(result.testResult, result.controlResult));

    }


    public Experiment<T> compare(BiFunction<T, T, Object> compare) {
        comparators.add(compare);
        return this;
    }


}
