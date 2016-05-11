package algorithms.dataStructures.map;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 26.10.2015.
 */
public class ArrayMap<K extends Comparable, V> implements Map<K, V> {
    private static final int MIN_CAPACITY = 10;
    private K[] keys;
    private V[] values;
    private int size;
    private int capacity;
    private Comparator comparator;

    public ArrayMap() {
        keys = (K[]) new Comparable[capacity = MIN_CAPACITY];
        values = (V[]) new Object[capacity];
        size = 0;
        comparator = new DefaultComparator<>();
    }

    public ArrayMap(Comparator comparator) {
        keys = (K[]) new Comparable[capacity = MIN_CAPACITY];
        values = (V[]) new Object[capacity];
        size = 0;
        this.comparator = comparator;
    }


    @Override
    public void put(K key, V value) {
        System.out.println("Putting...");
        if (size == 0) {
            keys[0] = key;
            values[size++] = value;
        } else {
            int index = findInd(key, 0, size - 1);
            if (comparator.compare(keys[index], key) == 0) {
                values[index] = value;
            } else {
                if (size >= capacity) {
                    changeArrayCapacity(capacity / 4);
                }
                for (int i = index + 2; i < size; i--) {
                    keys[i + 1] = keys[i];
                    values[i + 1] = values[i];
                }
                keys[index + 1] = key;
                values[index + 1] = value;
                size++;
            }
        }
        print();
    }

    @Override
    public V get(K key) {
        System.out.println("Getting...");
        print();
        if (size <= 0) {
            return null;
        }
        int index = findInd(key, 0, size - 1);
        if (comparator.compare(keys[size()], key) == 0) {
            return values[index];
        }
        return null;
    }

    @Override
    public void remove(K key) {
        if (size != 0) {
            int index = findInd(key, 0 , size - 1);
            if (comparator.compare(keys[index], key) == 0) {
                for (int i = size - 1; i > index; i--) {
                    keys[i - 1] = keys[i];
                    values[i - 1] = values[i];
                }
                keys[--size] = null;
                values[size] = null;
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        int index = findInd(key, 0, size - 1);
        return comparator.compare(keys[index], key) == 0;
    }

    @Override
    public boolean containsValue(V value) {
        for (V val: values) {
            if (comparator.compare(val, value) == 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Iterable<K> keys() {
        return new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    int keyIndex = 0;

                    @Override
                    public boolean hasNext() {
                        return size > keyIndex;
                    }

                    @Override
                    public K next() {
                        if (keyIndex < size) {
                            return keys[keyIndex++];
                        }
                        return null;
                    }
                };
            }
        };
    }

    @Override
    public Iterable<V> values() {
        return new Iterable<V>() {
            @Override
            public Iterator<V> iterator() {
                return new Iterator<V>() {
                    int valIndex = 0;

                    @Override
                    public boolean hasNext() {
                        return size > valIndex;
                    }

                    @Override
                    public V next() {
                        if (valIndex < size) {
                            return values[valIndex++];
                        }
                        return null;
                    }
                };
            }
        };
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    /*
    This is a Binary Search algorithm. It returns an index of element if it has been found in array.
    If there is no such element the method returns an index of element that is the last less element in array.
     */
    private int findInd(K key, int min, int max) {
        if (min < max) {
            int mid = min + (max - min) / 2;
            if (comparator.compare(key, keys[mid]) < 0) {
                return findInd(key, min, mid - 1);
            } else if (comparator.compare(key, keys[mid]) > 0) {
                return findInd(key, mid + 1, max);
            } else {
                return mid;
            }
        } else {
            return min;
        }
    }

    private static int findInd(int[] array, int key, int min, int max) {
        if (min < max) {
            int mid = min + (max - min) / 2;
            if (key < array[mid]) {
                return findInd(array, key, min, mid - 1);
            } else if (key > array[mid]) {
                return findInd(array, key, mid + 1, max);
            } else {
                return mid;
            }
        } else {
            return min;
        }
    }

    protected void changeArrayCapacity(int diff) {
        capacity = Math.max(MIN_CAPACITY, capacity + diff);

        Comparable[] tempKeys = new Comparable[size];
        Comparable[] tempValues = new Comparable[size];

        System.arraycopy(keys, 0, tempKeys, 0, size);
        System.arraycopy(values, 0, tempValues, 0, size);

        keys = (K[]) new Object[capacity];
        values = (V[]) new Object[capacity];

        System.arraycopy(tempKeys, 0, keys, 0, size);
        System.arraycopy(tempValues, 0, values, 0, size);
        System.out.println("Changing cap " + capacity);
    }

    private void print() {
        System.out.println("size: " + size);
        System.out.println(Arrays.toString(keys));
        System.out.println(Arrays.toString(values));
    }

    public class DefaultComparator<T extends Comparable> implements Comparator<T> {
        @Override
        public int compare(T o1, T o2) {
            if (o1 == null || o2 == null ||
                    !o1.getClass().equals(o2.getClass())) {
                return 0;
            } else {
                return o1.compareTo(o2);
            }
        }
    }

    public static void main(String[] args) {
//        int[] array = {};
//        System.out.println(findInd(array, 5, 0, array.length - 1));
        Map<Integer, String> map = new ArrayMap<>();
        map.put(1, "Taras");
        map.put(2, "Igor");
//        System.out.println(map.getByIndex(1));
//
//        map.remove(1);
//        System.out.println(map.getByIndex(1));

        for (String val: map.values()) {
            System.out.println(val.toString());
        }
    }


}
