package algorithms.sorting.quick;

import algorithms.sorting.SortClient;
import algorithms.sorting.SortingAlgorithm;

import javax.swing.text.DefaultEditorKit;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Taras.Mykulyn on 20.10.2015.
 */
public class QuickSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        sort(array, 0, array.length - 1);
    }

    private void sort(Comparable[] array, int min, int max) {
        if (min < max) {
            int center = arrange(array, min, max);
            sort(array, min, center);
            sort(array, center + 1, max);
        }
    }

    private int arrange(Comparable[] array, int min, int max) {
        int pivotIndex = min + (max - min) / 2;
        Comparable pivot = array[pivotIndex];

        int firstPartIndex = min;
        int secondPartIndex = max;

        while (true) {
            while (array[firstPartIndex].compareTo(pivot) < 0 && firstPartIndex < max) {
                firstPartIndex++;
            }
            while (array[secondPartIndex].compareTo(pivot) > 0 && secondPartIndex > 0) {
                secondPartIndex--;
            }
            if (firstPartIndex < secondPartIndex) {
                exchange(array, firstPartIndex++, secondPartIndex--);
            } else {
                break;
            }
        }
        return secondPartIndex;
    }

    private void exchange(Comparable[] array, int i, int j) {
        Comparable t = array[i];
        array[i] = array[j];
        array[j] = t;
    }
}
