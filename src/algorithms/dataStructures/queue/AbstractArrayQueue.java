package algorithms.dataStructures.queue;

/**
 * Created by Taras.Mykulyn on 23.10.2015.
 */
public abstract class AbstractArrayQueue implements Queue<Comparable> {
    protected static int MIN_CAPACITY = 10;
    protected int max = Integer.MAX_VALUE;
    protected int capacity = MIN_CAPACITY;
    protected Comparable[] array;
    protected int size;

    @Override
    public int size() {
        return 0;
    }

    protected void changeArrayCapacity(int diff) {
        capacity = Math.max(MIN_CAPACITY, capacity + diff);

        Comparable[] temp = new Comparable[size];
        System.arraycopy(array, 0, temp, 0, size);
        array = new Comparable[capacity];
        System.arraycopy(temp, 0, array, 0, size);
        System.out.println("Changing cap " + capacity);
    }

    protected void exch(Comparable[] array, int i, int j) {
        Comparable temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

}
