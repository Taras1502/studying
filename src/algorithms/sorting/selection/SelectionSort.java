package algorithms.sorting.selection;

import algorithms.sorting.SortingAlgorithm;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class SelectionSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {

    }

    @Override
    public void sort(int[] array) {
        int minInd;
        for (int i = 0; i < array.length; i++) {
            minInd = i;
            for (int j = i; j < array.length; j++) {
                if (array[j] < array[minInd]) {
                    minInd = j;
                }
            }
            int t = array[i];
            array[i] = array[minInd];
            array[minInd] = t;
        }
    }

}
