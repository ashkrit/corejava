package org.experiment.scientist.app.core;

import org.experiment.scientist.app.algorithim.BubbleSort;
import org.experiment.scientist.app.algorithim.LanguageSort;
import org.experiment.scientist.app.algorithim.Sorting;
import org.experiment.scientist.core.Experiment;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentApp {

    public static void main(String[] args) {
        Experiment<String[]> experiment = new Experiment("Sorting Experiment");


        experiment
                .withControl(ExperimentApp::controlLogic)
                .withCandidate(ExperimentApp::candidateLogic)
                .compareResult(compareArray())
                .compareResult(compareLength())
                .times(10)
                .parallel()
                .run().publish();


    }

    private static BiFunction<String[], String[], Object> compareArray() {
        return (control, candidate) -> {
            try {
                assertArrayEquals(control, candidate);
                return String.format(LocalDateTime.now() + " - Matched Content Head (%s,%s) , Tail  (%s,%s) ", control[0], candidate[0], control[control.length - 1], candidate[candidate.length - 1]);
            } catch (Exception e) {
                return e.getMessage();
            }
        };
    }

    private static BiFunction<String[], String[], Object> compareLength() {
        return (control, candidate) -> {
            try {
                assertEquals(control.length, candidate.length);
                return String.format(LocalDateTime.now() + " - Matched Length %s = %s", control.length, candidate.length);
            } catch (Exception e) {
                return e.getMessage();
            }
        };
    }

    private static String[] candidateLogic() {
        String[] strings = {"Z", "A", "C", "B"};
        Sorting<String> sort = new LanguageSort<>();
        sort.sort(strings);
        return strings;
    }

    private static String[] controlLogic() {
        String[] strings = {"Z", "A", "C", "B"};
        Sorting<String> sort = new BubbleSort<>();
        sort.sort(strings);
        return strings;
    }
}
