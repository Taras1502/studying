package algorithms.dataStructures.queue;

import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 22.10.2015.
 */
public class PriorityQueue extends AbstractArrayQueue {
    public PriorityQueue() {
        array = new Comparable[capacity];
        size = 0;
    }

    @Override
    public void enqueue(Comparable element) {
        if (size >= capacity) {
            changeArrayCapacity(capacity / 4);
        }
        array[size++] = element;
        System.out.println("Enqueue: " + Arrays.toString(array));
    }

    @Override
    public Comparable dequeue() {
        Comparable element = get();
        if (element != null) {
            array[size - 1] = null;
            size--;
        }
        System.out.println("Dequeue: " + Arrays.toString(array));
        return element;
    }

    @Override
    public Comparable get() {
        if (size > 0) {
            setMax();
            if (size < capacity / 2 && capacity > MIN_CAPACITY) {
                changeArrayCapacity(-capacity / 4);
            }
            Comparable element = array[size - 1];
            System.out.println("Get: " + Arrays.toString(array));
            return element;
        }
        return null;
    }

    private void setMax() {
        System.out.println(Arrays.toString(array));
        if (size > 1) {
            for (int i = size - 1; i >= 0; i--) {
                if (array[i].compareTo(array[size - 1]) > 0) {
                    Comparable t = array[i];
                    array[i] = array[size - 1];
                    array[size - 1] = t;
                }
            }
        }
    }

    public static void main(String[] args) {
        PriorityQueue queue = new PriorityQueue();
    }
}
