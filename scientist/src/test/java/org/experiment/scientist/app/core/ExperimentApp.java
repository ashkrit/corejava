package org.experiment.scientist.app.core;

import org.experiment.scientist.app.algorithim.BubbleSort;
import org.experiment.scientist.app.algorithim.LanguageSort;
import org.experiment.scientist.app.algorithim.Sorting;
import org.experiment.scientist.core.Experiment;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentApp {

    public static void main(String[] args) {
        Experiment<String[], String[]> experiment = new Experiment("Sorting Experiment");


        experiment
                .withControl(ExperimentApp::controlLogic)
                .withCandidate(ExperimentApp::candidateLogic)
                .withParamGenerator(() -> itemsToSort())
                .compareResult("Array Length", compareLength())
                .compareResult("Array Content", compareArray())
                .times(1000)
                .parallel()
                .run()
                .publish();


    }

    private static String[] itemsToSort() {
        List<String> original = toAlphabets();
        Collections.shuffle(original);

        int fixedItem = 0;
        int seed = ThreadLocalRandom.current().nextInt(original.size() - fixedItem);
        String[] shuffleItems = original.toArray(new String[]{});
        return Arrays.copyOf(shuffleItems, fixedItem + seed);
    }

    private static List<String> toAlphabets() {
        Stream<Character> lowerCase = Stream
                .iterate((char) 97, i -> (char) (i + 1))
                .limit(26);

        Stream<Character> upperCase = Stream
                .iterate((char) 65, i -> (char) (i + 1))
                .limit(26);

        return Stream.concat(upperCase, lowerCase)
                .map(String::valueOf)
                .collect(Collectors.toList());


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

    private static String[] candidateLogic(String[] param) {

        String[] cloneValue = Arrays.copyOf(param, param.length);
        Sorting<String> sort = new LanguageSort<>();
        sort.sort(cloneValue);
        return cloneValue;
    }

    private static String[] controlLogic(String[] param) {
        String[] cloneValue = Arrays.copyOf(param, param.length);
        Sorting<String> sort = new BubbleSort<>();
        sort.sort(cloneValue);
        return cloneValue;
    }
}
