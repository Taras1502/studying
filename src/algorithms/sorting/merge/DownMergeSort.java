package algorithms.sorting.merge;

import algorithms.sorting.SortClient;
import algorithms.sorting.SortingAlgorithm;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by macbookpro on 10/19/15.
 */
public class DownMergeSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        int n = array.length;
        Comparable[] aux = new Comparable[n];
        sort(array, aux, 0, n - 1);
    }

    private void sort(Comparable[] array, Comparable[] aux, int min, int max) {
        if (min < max) {
            int mid = min + (max - min) / 2;
            sort(array, aux, min, mid);
            sort(array, aux, mid + 1, max);
            merge(array, aux, min, mid, max);
        }
    }

    private void merge(Comparable[] array, Comparable[] aux, int min, int mid, int max) {
        if (min < max) {
            int firstPartIndex = min;
            int secondPartIndex = mid + 1;
            for (int i = min; i <= max; i++) {
                aux[i] = array[i];
            }

            for (int i = min; i <= max; i++) {
                if (firstPartIndex > mid) {
                    array[i] = aux[secondPartIndex++];
                } else if (secondPartIndex > max) {
                    array[i] = aux[firstPartIndex++];
                } else if (aux[firstPartIndex].compareTo(aux[secondPartIndex]) < 0) {
                    array[i] = aux[firstPartIndex++];
                } else {
                    array[i] = aux[secondPartIndex++];
                }
            }
        }
    }
}
