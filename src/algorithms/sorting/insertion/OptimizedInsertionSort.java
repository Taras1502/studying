package algorithms.sorting.insertion;

import algorithms.sorting.SortingAlgorithm;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class OptimizedInsertionSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {

    }

    @Override
    public void sort(int[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            for (int j = i; j > 0; j--) {
                if (j > 1 && array[j] < array[j - 2]) {
                    int t = array[j - 2];
                    array[j - 2] = array[j];
                    array[j] = array[j - 1];
                    array[j - 1] = t;
                    j--;
                } else if (array[j] < array[j - 1]) {
                    int t = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = t;
                } else {
                    break;
                }
            }
        }
    }
}
