package org.experiment.scientist.app.core;

import org.experiment.scientist.app.algorithim.BubbleSort;
import org.experiment.scientist.app.algorithim.LanguageSort;
import org.experiment.scientist.app.algorithim.Sorting;
import org.experiment.scientist.core.Experiment;
import org.junit.jupiter.api.Assertions;

import java.util.function.BiFunction;

public class ExperimentApp {

    public static void main(String[] args) {
        Experiment<String[]> experiment = new Experiment("Sorting Experiment");

        BiFunction<String[], String[], Object> c = (tValue, cValue) -> {
            try {
                Assertions.assertArrayEquals(tValue, cValue);
                return "";
            } catch (Exception e) {
                return e.getMessage();
            }
        };

        experiment
                .withTest(() -> currentImpl())
                .withControl(() -> newImpl())
                .compare(c)
                .run();


    }

    private static String[] newImpl() {
        String[] strings = {"Z", "A", "C", "B"};
        Sorting<String> sort = new LanguageSort<>();
        sort.sort(strings);
        return strings;
    }

    private static String[] currentImpl() {
        String[] strings = {"Z", "A", "C", "B"};
        Sorting<String> sort = new BubbleSort<>();
        sort.sort(strings);
        return strings;
    }
}
