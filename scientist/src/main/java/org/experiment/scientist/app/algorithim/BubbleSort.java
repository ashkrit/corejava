package org.experiment.scientist.app.algorithim;

public class BubbleSort<V extends Comparable<V>> implements Sorting<V> {
    public void sort(V[] values) {

        for (int index = 1; index < values.length; index++) {
            sortSlice(values, index);
        }

    }

    private void sortSlice(V[] values, int index) {
        for (int current = index; current > 0; current--) {

            int previous = current - 1;
            int compareResult = values[current].compareTo(values[previous]);
            if (compareResult < 0) {
                swap(values, current, previous);
            }

        }
    }

    private void swap(V[] values, int index1, int index2) {
        V temp = values[index1];
        values[index1] = values[index2];
        values[index2] = temp;
    }
}
