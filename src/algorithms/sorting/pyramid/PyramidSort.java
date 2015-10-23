package algorithms.sorting.pyramid;

import algorithms.sorting.SortClient;
import algorithms.sorting.SortingAlgorithm;

import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 23.10.2015.
 */
public class PyramidSort<T> implements SortingAlgorithm<T> {
    @Override
    public void sort(Comparable<T>[] array) {
        int n = array.length;
        for (int i = n / 2; i >= 0; i--) {
            moveDown(array, n - 1, i);
        }
        while (n > 0) {
            exch(array, 0, n - 1);
            moveDown(array, --n, 0);
        }

    }

    private void moveDown(Comparable[] array, int n, int k) {
        int childInd = 2 * k;
        while (childInd < n) {
            if (childInd + 1 < n && array[childInd].compareTo(array[childInd + 1]) < 0) {
                childInd++;
            }
            if (array[k].compareTo(array[childInd]) < 0) {
                exch(array, k, childInd);
                k = childInd;
                childInd = 2 * k;
            } else {
                break;
            }
        }
    }

    protected void exch(Comparable[] array, int i, int j) {
        Comparable temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static void main(String[] args) {
        Integer[] array = {4,5,7,3,2,6,89,6,0,6,4};

        PyramidSort<Integer> pyramidSort = new PyramidSort<>();
        pyramidSort.sort(array);

        System.out.println(SortClient.isSorted(array));
        System.out.println(Arrays.toString(array));
    }
}
