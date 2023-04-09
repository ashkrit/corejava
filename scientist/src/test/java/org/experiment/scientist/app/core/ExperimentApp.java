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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ExperimentApp {

    public static void main(String[] args) {
        Experiment<String[], String[]> experiment = new Experiment("Sorting Experiment");

        experiment
                .withControl("bubble sort", param -> {
                    String[] cloneValue = Arrays.copyOf(param, param.length);
                    Sorting<String> sort = new BubbleSort<>();
                    sort.sort(cloneValue);
                    return cloneValue;
                })
                .withCandidate("System sort", param -> {

                    String[] cloneValue = Arrays.copyOf(param, param.length);
                    Sorting<String> sort = new LanguageSort<>();
                    sort.sort(cloneValue);
                    return cloneValue;
                })

                .withParamGenerator(() -> itemsToSort())

                .compareResult("Array Length", (control, candidate) -> {
                    try {
                        assertEquals(control.length, candidate.length);
                        return String.format(LocalDateTime.now() + " - Matched Length %s = %s", control.length, candidate.length);
                    } catch (Exception e) {
                        return e.getMessage();
                    }
                })
                .compareResult("Array Content", (control1, candidate1) -> {
                    try {
                        assertArrayEquals(control1, candidate1);
                        return String.format(LocalDateTime.now() + " - Matched Content Head (%s,%s) , Tail  (%s,%s) ", control1[0], candidate1[0], control1[control1.length - 1], candidate1[candidate1.length - 1]);
                    } catch (Exception e1) {
                        return e1.getMessage();
                    }
                })
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

}
