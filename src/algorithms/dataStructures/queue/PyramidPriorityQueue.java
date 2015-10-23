package algorithms.dataStructures.queue;

import algorithms.stringCalculator.Commands;

import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 23.10.2015.
 */
public class PyramidPriorityQueue extends AbstractArrayQueue {

    public PyramidPriorityQueue() {
        array = new Comparable[capacity];
        size = 0;
    }

    @Override
    public void enqueue(Comparable element) {
        if (size >= capacity) {
            changeArrayCapacity(capacity / 4);
        }
        array[size] = element;
        moveUp(size);
        size++;
        System.out.println("ENQUEUE\tsize" + size + "\t" + Arrays.toString(array));
    }

    @Override
    public Comparable dequeue() {
        if (size > 0) {
            Comparable element = array[0];
            exch(array, 0, size - 1);
            array[size - 1] = null;
            size--;
            moveDown(0);
            if (size < capacity / 2 && capacity > MIN_CAPACITY) {
                changeArrayCapacity(-capacity / 4);
            }
            System.out.println("DEQUEUE\tsize" + size + "\t" + Arrays.toString(array));
            return element;
        } else {
            return null;
        }
    }

    @Override
    public Comparable get() {
        if (size > 0) {
            Comparable element = array[0];
            System.out.println("DEQUEUE\tsize" + size + "\t" + Arrays.toString(array));
            return element;
        } else {
            return null;
        }
    }

    private void moveDown(int k) {
        int childInd = 2 * k;
        while (childInd < size) {
            if (childInd + 1 < size && array[childInd].compareTo(array[childInd + 1]) < 0) {
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

    private void moveUp(int k) {
        int parentInd = k / 2;
        while (parentInd >= 0) {
            if (array[k].compareTo(array[parentInd]) > 0) {
                exch(array, k, parentInd);
                k = parentInd;
                parentInd = k / 2;
            } else {
                break;
            }
        }
    }

    public static void main(String[] args) {
        Queue queue = new PyramidPriorityQueue();
        for (int i = 0; i < 100; i++) {
            queue.enqueue(i);
        }

        for (int i = 0; i < 100; i++) {
            System.out.println(queue.dequeue());
        }
        System.out.println(queue.get());
    }

}
