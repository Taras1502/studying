package algorithms.sorting.insertion;

import algorithms.sorting.SortingAlgorithm;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class InsertionSort<T> implements SortingAlgorithm<T> {

    @Override
    public void sort(Comparable<T>[] array) {
        int n = array.length;
        for (int i = 1; i < n; i++) {
            for (int j = i; j > 0; j--) {
                if (array[j].compareTo((T) array[j - 1]) < 0) {
                    Comparable<T> t = array[j - 1];
                    array[j - 1] = array[j];
                    array[j] = t;
                } else {
                    break;
                }
            }
        }
    }
}
