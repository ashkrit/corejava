package org.experiment.scientist.app.core;

import org.experiment.scientist.app.algorithim.BubbleSort;
import org.experiment.scientist.app.algorithim.LanguageSort;
import org.experiment.scientist.app.algorithim.Sorting;
import org.experiment.scientist.core.Experiment;

import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ExperimentApp {

    public static void main(String[] args) {
        Experiment<String[]> experiment = new Experiment("Sorting Experiment");

        BiFunction<String[], String[], Object> c = (tValue, cValue) -> {
            try {
                assertArrayEquals(tValue, cValue);
                return String.format("Matched, Head (%s,%s) , Tail  (%s,%s) ",
                        tValue[0], cValue[0],
                        tValue[tValue.length - 1], cValue[cValue.length - 1]);
            } catch (Exception e) {
                return e.getMessage();
            }
        };

        experiment.withControl(ExperimentApp::controlLogic).withCandidate(ExperimentApp::candidateLogic).compareResult(c).run();


        experiment.publish();


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
