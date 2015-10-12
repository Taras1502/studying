package algorithms.sorting;

import algorithms.sorting.insertion.InsertionSort;
import algorithms.sorting.insertion.OptimizedInsertionSort;
import algorithms.sorting.selection.SelectionSort;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class SortClient {
    public static void sort(SortingAlgorithm sortingAlgorithm, int[] array) {
        sortingAlgorithm.sort(array);
    }

    public static void print(int[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static boolean isSorted(int[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i] > array[i + 1]) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Random rand = new Random();
        int[] array = new int[4000];
        int[] array1 = new int[4000];
        int[] array2 = new int[4000];
        for (int i = 0 ; i < array.length; i++) {
            array[i] = array1[i] = array2[i] = rand.nextInt();
        }
        long start = System.currentTimeMillis();

        sort(new OptimizedInsertionSort(), array2);
        long point1 = System.currentTimeMillis();

        sort(new InsertionSort(), array1);
        long point2 = System.currentTimeMillis();

        sort(new SelectionSort(), array);
        long point3 = System.currentTimeMillis();

        System.out.println("\nOPTIMIZED INSERTION SORT");
        System.out.println("sorted: " + isSorted(array));
        System.out.println("time: " + (point1 - start));

        System.out.println("\nINSERTION SORT");
        System.out.println("sorted: " + isSorted(array1));
        System.out.println("time: " + (point2 - point1));

        System.out.println("\nSELECTION SORT");
        System.out.println("sorted: " + isSorted(array2));
        System.out.println("time: " + (point3 - point2));

    }
}
