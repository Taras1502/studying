package algorithms.dataStructures.map;

import java.util.Random;

/**
 * Created by Taras.Mykulyn on 18.11.2015.
 */

@SuppressWarnings("unchecked")
class OpenAddressHashMap<K extends Comparable, V extends Comparable> implements Map<K, V> {
    private static final int DEFAULT_MIN_CAPACITY = 12;
    private static final double DEFAULT_LOAD_FACTOR = 0.75;

    private K[] keys;
    private V[] values;
    private int size;
    private int capacity;
    private double loadFactor;
    private int maxArraySize;
    private int minArraySize;

    public OpenAddressHashMap() {
        this(DEFAULT_MIN_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public OpenAddressHashMap(int capacity, double loadFactor) {
        if (capacity < DEFAULT_MIN_CAPACITY) {
            throw new IllegalArgumentException("Map capacity must be at least " + DEFAULT_MIN_CAPACITY);
        }
        this.loadFactor = loadFactor;
        this.capacity = capacity;
        maxArraySize = (int) (loadFactor * capacity);
        minArraySize = Math.max(DEFAULT_MIN_CAPACITY, (int) ((capacity * loadFactor) / 2));

        keys = (K[]) new Comparable[capacity];
        values = (V[]) new Comparable[capacity];
        size = 0;
    }

    @Override
    public void put(K key, V value) {
        int keyHash = key.hashCode();
        int index = keyHash % capacity;
        System.out.println("putting [" + key + ", " + value + "]" + "index " + index);

        if (size > maxArraySize) {
            resize();
        }

        while (keys[index] != null) {
            if (keys[index].equals(key)) {
                break;
            } else {
                index = (index + 1) % capacity;
            }
        }
        keys[index] = key;
        values[index] = value;
        size++;

    }

    @Override
    public V get(K key) {
        int index = indexOf(key);
        if (index > -1) {
            return values[index];
        }
        return null;
    }

    @Override
    public void remove(K key) {
        System.out.println("removing [" + key + "]");
        int index = indexOf(key);
        if (index < 0) {
            return;
        }
        keys[index] = null;
        values[index] = null;
        index = (index + 1) % capacity;
        while (keys[index] != null) {
            K keyToRePut = keys[index];
            V valueToRePut = values[index];
            keys[index] = null;
            values[index] = null;
            size--;
            put(keyToRePut, valueToRePut);
            index = (index + 1) % capacity;
        }
        size--;
        if (size < minArraySize) {
            resize();
        }
    }

    @Override
    public boolean containsKey(K key) {
        return indexOf(key) > -1;
    }

    private int indexOf(K key) {
        int keyHash = key.hashCode();
        int index = keyHash % capacity;
        int i = 0;
        while(i < capacity) {
            if (key.equals(keys[index])) {
                return index;
            }
            index = (index + 1) % capacity;
            i++;
        }
        return -1;
    }

    @Override
    public boolean containsValue(V value) {
        return false;
    }

    @Override
    public Iterable<K> keys() {
        return null;
    }

    @Override
    public Iterable<V> values() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public int size() {
        return 0;
    }

    private void resize() {
        int tempKeysSize = capacity;
        K[] tempKeys = (K[]) new Comparable[tempKeysSize];
        V[] tempValues = (V[]) new Comparable[tempKeysSize];
        System.arraycopy(keys, 0, tempKeys, 0, tempKeysSize);
        System.arraycopy(values, 0, tempValues, 0, tempKeysSize);

        if (size > maxArraySize) {
            capacity *= 2;
        } else {
            capacity /=2;
        }
        System.out.println("resizing capacity to " + capacity);

        maxArraySize = (int) (loadFactor * capacity);
        minArraySize = Math.max(DEFAULT_MIN_CAPACITY, (int) ((capacity * loadFactor) / 2));
        System.out.println("maxArraySize " + maxArraySize);

        keys = (K[]) new Comparable[capacity];
        values = (V[]) new Comparable[capacity];
        size = 0;

        K tempKey;
        for (int i = 0; i < tempKeysSize; i++) {
            tempKey = tempKeys[i];
            if (tempKey != null) {
                put(tempKey, tempValues[i]);
            }
        }
    }

    public static void main(String[] args) {
        Map<Integer, String> map = new OpenAddressHashMap<>();
        Random rand = new Random();

        int r = rand.nextInt(1000000) + 0;
        map.put(r, "hdg");
        for (int i = 0; i < 100; i++) {
            map.put(rand.nextInt(1000000) + 0, String.valueOf(i));
        }

        map.remove(r);
    }
}
