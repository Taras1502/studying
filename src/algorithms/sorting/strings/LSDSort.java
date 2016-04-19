package algorithms.sorting.strings;

import java.util.Arrays;
import java.util.Random;

/**
 * Created by macbookpro on 11/23/15.
 */
public class LSDSort {
    public static void sort(String[] array) {
        int R = 256;
        int d = array[0].length() - 1;
        int arrayLength = array.length;
        String[] aux = new String[arrayLength];
        int index;
        while (d >= 0) {
            int[] counter = new int[R + 1];
            for (String anArray : array) {
                index = anArray.charAt(d) + 1;
                counter[index]++;
            }
            for (int i = 1; i < R + 1; i++) {
                counter[i] += counter[i - 1];
            }
            for (String str : array) {
                index = counter[str.charAt(d)]++;
                aux[index] = str;
            }
            System.arraycopy(aux, 0, array, 0, arrayLength);
            d--;
        }
    }

    public static void main(String[] args) {
        String[] arr = new String[100000];
        Random rand = new Random();
        for (int i = 0; i < 100000; i++) {
            arr[i] = String.valueOf(rand.nextInt(1000000000)+1000000000);
        }
        sort(arr);
        System.out.println(Arrays.toString(arr));
    }
}
