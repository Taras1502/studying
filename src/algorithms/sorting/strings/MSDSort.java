package algorithms.sorting.strings;

import algorithms.sorting.SortClient;
import algorithms.stringCalculator.StringCalculator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by macbookpro on 11/24/15.
 */
public class MSDSort {
    private static final int R = 256;
    public static void sort(String[] array) {
        int arrayLength = array.length;
        sort(array, 0, arrayLength - 1, 0);
    }

    private static void sort(String[] array, int start, int end, int d) {
        if (!(end > start)) return;
        int[] counter = new int[R + 2];
        int index;
        String str;
        boolean needToSort = false;
        for (int i = start; i <= end; i++) {
            str = array[i];
            if (str.length() > d) {
                index = str.charAt(d) + 2;
                needToSort = true;
            } else {
                index = 1;
            }
            counter[index]++;
        }
        if (!needToSort) return;
        for (int i = 1; i < R + 2; i++) {
            counter[i] += counter[i - 1];
        }
        String[] aux = new String[end - start + 1];
        for (int i = start; i <= end; i++) {
            str = array[i];
            if (str.length() > d) {
                index = counter[str.charAt(d) + 1]++;
            } else {
                index = counter[0]++;
            }
            aux[index] = str;
        }
        int st = start;
        for (String s: aux) {
            array[st++] = s;
        }
        ArrayList<String> list = new ArrayList<>();
        Collections.unmodifiableList(list);
        for (int i = 1; i < R + 2; i++) {
            if (counter[i] > counter[i-1]) {
                sort(array,start + counter[i-1], start + counter[i] - 1, d + 1);
            }
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String[] arr = new String[1000000];
        Random rand = new Random();
        for (int i = 0; i < 1000000; i++) {
            arr[i] = String.valueOf(rand.nextLong() + 1);
        }
//        System.out.println(Arrays.toString(arr));
        sort(arr);
        System.out.println(Arrays.toString(arr));

        System.out.println(SortClient.isSorted(arr));
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(arr.length* Long.BYTES / 1024 / 1024);
    }
}
