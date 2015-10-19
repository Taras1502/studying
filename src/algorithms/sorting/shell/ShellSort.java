package algorithms.sorting.shell;

import algorithms.sorting.SortingAlgorithm;

/**
 * Created by macbookpro on 10/14/15.
 */
public class ShellSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        int n = array.length;
        int h = 1;

        while (h < n/3) {
            h = 3 * h + 1;
        }

        while (h >= 1) {
            for (int i = h; i < n; i++) {
                for (int j = i; j >= h; j -= h) {
                    if (array[j].compareTo((T) array[j - h]) < 0) {
                        Comparable<T> t = array[j];
                        array[j] = array[j - h];
                        array[j - h] = t;
                    } else {
                        break;
                    }
                }
            }
            h /= 3;
        }
    }
}
