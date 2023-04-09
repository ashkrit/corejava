package org.experiment.scientist.core;

import java.util.function.Function;

public class ExperimentFunction<I, O> {


    public final String name;
    public final Function<I, O> fn;

    public ExperimentFunction(String name, Function<I, O> fn) {
        this.name = name;
        this.fn = fn;
    }
}
