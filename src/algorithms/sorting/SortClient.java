package algorithms.sorting;

import algorithms.sorting.insertion.InsertionSort;
import algorithms.sorting.insertion.OptimizedInsertionSort;
import algorithms.sorting.merge.DownMergeSort;
import algorithms.sorting.merge.UpMergeSort;
import algorithms.sorting.quick.QuickSort;
import algorithms.sorting.selection.SelectionSort;
import algorithms.sorting.shell.ShellSort;
import java.util.Arrays;
import java.util.Random;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class SortClient {
    public static void sort(SortingAlgorithm sortingAlgorithm, Comparable[] array) {
        sortingAlgorithm.sort(array);
    }

    public static void print(Comparable[] array) {
        System.out.println(Arrays.toString(array));
    }

    public static boolean isSorted(Comparable[] array) {
        for (int i = 0; i < array.length - 1; i++) {
            if (array[i].compareTo(array[i + 1]) > 0) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        Random rand = new Random();
        Integer[] array1 = new Integer[100000];
        Integer[] array2 = new Integer[100000];
        Integer[] array3 = new Integer[100000];
        Integer[] array4 = new Integer[100000];
        Integer[] array5 = new Integer[100000];
        Integer[] array6 = new Integer[100000];
        Integer[] array7 = new Integer[100000];
        for (int i = 0 ; i < array1.length; i++) {
            array1[i] =
            array2[i] =
            array3[i] =
            array4[i] =
            array5[i] =
            array6[i] =
            array7[i] =
            rand.nextInt();
        }

        long point1 = System.currentTimeMillis();

        sort(new ShellSort(), array1);
        long point2 = System.currentTimeMillis();

        sort(new InsertionSort(), array2);
        long point3 = System.currentTimeMillis();

        sort(new SelectionSort(), array3);
        long point4 = System.currentTimeMillis();

        sort(new OptimizedInsertionSort(), array4);
        long point5 = System.currentTimeMillis();

        sort(new UpMergeSort(), array5);
        long point6 = System.currentTimeMillis();

        sort(new DownMergeSort<>(), array6);
        long point7 = System.currentTimeMillis();

        sort(new QuickSort<>(), array7);
        long point8 = System.currentTimeMillis();

        System.out.println("\nSHELL SORT");
        System.out.println("sorted: " + isSorted(array1));
        System.out.println("time: " + (point2 - point1));

        System.out.println("\nINSERTION SORT");
        System.out.println("sorted: " + isSorted(array2));
        System.out.println("time: " + (point3 - point2));

        System.out.println("\nSELECTION SORT");
        System.out.println("sorted: " + isSorted(array3));
        System.out.println("time: " + (point4 - point3));

        System.out.println("\nOPTIMIZED INSERTION SORT");
        System.out.println("sorted: " + isSorted(array4));
        System.out.println("time: " + (point5 - point4));

        System.out.println("\nUP MERGE SORT");
        System.out.println("sorted: " + isSorted(array3));
        System.out.println("time: " + (point6 - point5));

        System.out.println("\nDOWN MERGE SORT");
        System.out.println("sorted: " + isSorted(array4));
        System.out.println("time: " + (point7 - point6));

        System.out.println("\nQUICK SORT");
        System.out.println("sorted: " + isSorted(array7));
        System.out.println("time: " + (point8 - point7));
    }
}
