package algorithms.dataStructures.map;

/**
 * Created by Taras.Mykulyn on 17.11.2015.
 */
public class HashMap<K, V> implements Map<K, V> {
    private static final int MIN_MAP_CAPACITY = 11;

    private Entry<K, V>[] array;
    private int size;
    private int capacity;

    public HashMap() {
        array = new Entry[capacity = MIN_MAP_CAPACITY];
        size = 0;
    }

    @Override
    public void put(K key, V value) {
        Entry<K, V> entryToInsert = new Entry<>(key, value);
        int index = entryToInsert.keyHash % capacity;
//        System.out.println("Putting " + key + " " + value + " to index " + index);
        if (array[index] == null) {
            array[index] = entryToInsert;
        } else {
            Entry<K, V> sameIndexEntry = array[index];
            while(sameIndexEntry.nextEntry != null) {
                if (sameIndexEntry.key.equals(entryToInsert.key)) {
                    return;
                } else {
                    sameIndexEntry = sameIndexEntry.nextEntry;
                }
            }
            if (!sameIndexEntry.key.equals(entryToInsert.key)) {
                sameIndexEntry.nextEntry = entryToInsert;
            }
        }
    }

    @Override
    public V get(K key) {
        int keyHash = key.hashCode();
        int index = keyHash % capacity;
        Entry<K, V> indexEntry = array[index];
        while (indexEntry != null) {
            if (indexEntry.key.equals(key)) {
                return indexEntry.value;
            } else {
                indexEntry = indexEntry.nextEntry;
            }
        }
        return null;
    }

    @Override
    public void remove(K key) {
        int index = key.hashCode() % capacity;
        if (array[index] != null) {
            Entry<K, V> entryToRemove = array[index];
            if (entryToRemove.key.equals(key)) {
                array[index] = entryToRemove.nextEntry;
                size--;
            } else {
                while (entryToRemove.nextEntry != null) {
                    if (entryToRemove.nextEntry.key.equals(key)) {
                        entryToRemove.nextEntry = entryToRemove.nextEntry.nextEntry;
                        size--;
                        break;
                    } else {
                        entryToRemove = entryToRemove.nextEntry;
                    }
                }
            }
        }
    }

    @Override
    public boolean containsKey(K key) {
        return false;
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

    static class Entry<K, V> {
        Entry<K, V> nextEntry;
        K key;
        V value;
        int keyHash;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            keyHash = calculateHash();
            nextEntry = null;
        }

        private int calculateHash() {
            return key.hashCode();
        }
    }

    public static void main(String[] args) {
        Map<Integer, String> map = new HashMap<>();

        System.out.println(map.get(1));
    }
}
