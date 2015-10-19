package algorithms.sorting.selection;

import algorithms.sorting.SortingAlgorithm;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class SelectionSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        int minInd;
        for (int i = 0; i < array.length; i++) {
            minInd = i;
            for (int j = i; j < array.length; j++) {
                if (array[j].compareTo((T) array[minInd]) < 0) {
                    minInd = j;
                }
            }
            Comparable<T> t = array[i];
            array[i] = array[minInd];
            array[minInd] = t;
        }
    }
}
