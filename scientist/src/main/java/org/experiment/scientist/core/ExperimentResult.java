package org.experiment.scientist.core;

public class ExperimentResult<T> {

    public final T testResult;
    public final T controlResult;

    public ExperimentResult(T testResult, T controlResult) {
        this.testResult = testResult;
        this.controlResult = controlResult;
    }
}
