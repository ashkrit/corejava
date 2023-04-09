package org.experiment.scientist.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Experiment<I, O> {
    private final String name;
    private Function<I, O> control;
    private Function<I, O> candidate;

    private final Map<String, BiFunction<O, O, Object>> resultComparator = new HashMap<>();
    private final ConcurrentMap<Long, List<ExperimentCompareResult>> compareResults = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private int times = 1;
    private boolean parallel;

    private Supplier<I> paramSupplier;

    public Experiment(String name) {
        this.name = name;
    }

    public Experiment<I, O> withControl(Function<I, O> control) {
        this.control = control;
        return this;
    }

    public Experiment<I, O> withCandidate(Function<I, O> candidate) {
        this.candidate = candidate;
        return this;
    }

    public Experiment<I, O> run() {

        compareResults.clear();

        Stream<I> stream = IntStream
                .range(0, times)
                .mapToObj($ -> paramSupplier.get());

        if (parallel) {
            stream = stream.parallel();
        }

        stream.forEach(this::_execute);

        return this;

    }

    private void _execute(I input) {
        ExperimentResult<O> result = new ExperimentResult<>(control.apply(input), candidate.apply(input));
        long nextSeq = sequence.incrementAndGet();
        List<ExperimentCompareResult> results = resultComparator
                .entrySet()
                .stream()
                .map(c -> _toExperimentResult(c.getKey(), c.getValue(), result))
                .collect(Collectors.toList());

        compareResults.put(nextSeq, results);
    }

    private ExperimentCompareResult _toExperimentResult(String name, BiFunction<O, O, Object> fn, ExperimentResult<O> result) {
        return new ExperimentCompareResult(name, fn.apply(result.control, result.candidate));
    }


    public Experiment<I, O> compareResult(String name, BiFunction<O, O, Object> compare) {
        resultComparator.put(name, compare);
        return this;
    }

    public Experiment<I, O> publish(Consumer<List<ExperimentCompareResult>> consumer) {

        compareResults
                .values()
                .forEach(consumer);
        return this;
    }

    public Experiment<I, O> publish() {
        System.out.println("Result for " + name);
        publish(r -> {
            String result = r.stream()
                    .map(v -> String.format("%s -> %s", v.name, v.value))
                    .collect(Collectors.joining(";"));
            System.out.println(result);
        });
        return this;
    }

    public Experiment<I, O> times(int times) {
        this.times = times;
        return this;
    }

    public Experiment<I, O> parallel() {
        this.parallel = true;
        return this;
    }

    public Experiment<I, O> withParamGenerator(Supplier<I> paramSupplier) {
        this.paramSupplier = paramSupplier;
        return this;
    }
}
