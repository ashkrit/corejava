package org.experiment.scientist.app.algorithim;

import java.util.Arrays;

public class LanguageSort<V extends Comparable<V>> implements Sorting<V> {
    public void sort(V[] values) {
        Arrays.sort(values);
    }
}
