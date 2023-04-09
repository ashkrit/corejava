package org.experiment.scientist.core;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Experiment<I, T> {
    private final String name;
    private Function<I, T> control;
    private Function<I, T> candidate;

    private final List<BiFunction<T, T, Object>> resultComparator = new CopyOnWriteArrayList<>();
    private ConcurrentMap<Long, List<Object>> compareResults = new ConcurrentSkipListMap<>();
    private final AtomicLong sequence = new AtomicLong();
    private int times = 1;
    private boolean parallel;

    private Supplier<I> param;

    public Experiment(String name) {
        this.name = name;
    }

    public Experiment<I, T> withControl(Function<I, T> control) {
        this.control = control;
        return this;
    }

    public Experiment<I, T> withCandidate(Function<I, T> candidate) {
        this.candidate = candidate;
        return this;
    }

    public Experiment<I, T> run() {

        compareResults.clear();


        Stream<I> stream = IntStream.range(0, times).mapToObj($ -> param.get());

        if (parallel) {
            stream = stream.parallel();
        }

        stream.forEach(this::_execute);

        return this;

    }

    private void _execute(I input) {
        ExperimentResult<T> result = new ExperimentResult<>(control.apply(input), candidate.apply(input));
        long nextSeq = sequence.incrementAndGet();
        List<Object> results = resultComparator.stream().map(c -> c.apply(result.control, result.candidate)).collect(Collectors.toList());

        compareResults.put(nextSeq, results);
    }


    public Experiment<I, T> compareResult(BiFunction<T, T, Object> compare) {
        resultComparator.add(compare);
        return this;
    }

    public Experiment<I, T> publish(Consumer<Object> consumer) {
        consumer.accept(name);
        compareResults.values().forEach(consumer);
        return this;
    }

    public Experiment<I, T> publish() {
        publish(System.out::println);
        return this;
    }

    public Experiment<I, T> times(int times) {
        this.times = times;
        return this;
    }

    public Experiment<I, T> parallel() {
        this.parallel = true;
        return this;
    }

    public Experiment<I, T> withParam(I param) {
        withParamGenerator(() -> param);
        return this;
    }

    public Experiment<I, T> withParamGenerator(Supplier<I> o) {
        this.param = o;
        return this;
    }
}
