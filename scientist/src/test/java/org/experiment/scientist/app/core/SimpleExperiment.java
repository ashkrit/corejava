package org.experiment.scientist.app.core;

import org.experiment.scientist.core.Experiment;

public class SimpleExperiment {

    public static void main(String[] args) {

        Experiment<Integer, Integer> experiment = new Experiment("Next Experiment");

        experiment
                .withControl("BitCount Using binary string", x ->
                        (int) Integer.toBinaryString(x)
                                .chars()
                                .filter(y -> y == '1')
                                .count()
                );

        experiment
                .withCandidate("BitCount using native", x -> Integer.bitCount(x));

        experiment
                .withParamGenerator(() -> 100)
                .compareResult("bit length", (control, candidate) -> control == candidate);

        experiment
                .times(100)
                .parallel()
                .run()
                .publish();


    }
}
