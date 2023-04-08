package org.experiment.scientist.app.algorithim;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class BubbleSortTest {

    @Test
    public void sort_string_values_using_shiny_algo() {

        String[] values = {"Z", "A", "D", "B", "C", "O"};
        Sorting<String> s = new BubbleSort<String>();

        s.sort(values);

        assertArrayEquals(new String[]{"A", "B", "C", "D", "O", "Z"}, values);
    }


    @Test
    public void verify_sort_results_from_native_sort_and_language_sort() {

        List<String> values = Arrays.asList("Z", "A", "D", "B", "C", "O");
        String[] langSortInputValues = values.toArray(new String[]{});
        String[] bubbleSortInputValues = values.toArray(new String[]{});

        Sorting<String> langSort = new LanguageSort<String>();
        Sorting<String> bubbleSort = new BubbleSort<String>();


        langSort.sort(langSortInputValues);
        bubbleSort.sort(bubbleSortInputValues);

        assertArrayEquals(langSortInputValues, bubbleSortInputValues);
    }
}
