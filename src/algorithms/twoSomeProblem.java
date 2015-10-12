package algorithms;

import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 08.10.2015.
 */
public class twoSomeProblem {
    public static int solveTwoSome(int[] array) {
        Arrays.sort(array);
        int count = 0;
        int index;
        for (int i = 0; i < array.length; i++) {
            index = Arrays.binarySearch(array, -array[i]);
            if (index > i) {
                count++;
            }
        }
        return count;
    }

    public static void main(String[] args) {
        int[] arr = {1, 2, -2, 3, 5, -1, -2, 2};
        System.out.println(solveTwoSome(arr));
    }
}
