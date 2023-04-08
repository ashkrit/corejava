package org.experiment.scientist.core;

public class ExperimentResult<T> {

    public final T control;
    public final T candidate;

    public ExperimentResult(T control, T candidate) {
        this.control = control;
        this.candidate = candidate;
    }
}
