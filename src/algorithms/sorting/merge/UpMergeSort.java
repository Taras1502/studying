package algorithms.sorting.merge;

import algorithms.sorting.SortClient;
import algorithms.sorting.SortingAlgorithm;
import algorithms.sorting.insertion.InsertionSort;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by macbookpro on 10/19/15.
 */
public class UpMergeSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        int n = array.length;
        Comparable[] aux = new Comparable[n];

        for (int h = 2; h / 2 <= n; h *= 2) {
            for (int i = 0; i <= n; i += h) {
                merge(array, aux, i, i + h / 2 - 1,
                        Math.min(i + h - 1, n - 1));
            }
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
