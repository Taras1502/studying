package algorithms.dataStructures.map;

/**
 * Created by Taras.Mykulyn on 26.10.2015.
 */
public interface Map<K, V> {
    /*
    Adds a new entry to the map.
    If an entry with the same key already exists, its value is replaced by a new one.
     */
    void put(K key, V value);

    /*
    Returns value found by key or null if there is no entry found.
     */
    V get(K key);

    /*
    Removes entry found by key.
     */
    void remove(K key);

    /*
    Returns true if key is found among entries in the map or false otherwise.
     */
    boolean containsKey(K key);

    /*
    Returns true if value is found among entries in the map or false otherwise.
     */
    boolean containsValue(V value);

    /*
    Returns an object of type Iterable that can iterate through all keys found in the map.
     */
    Iterable<K> keys();

    /*
    Returns an object of type Iterable that can iterate through all values found in the map.
     */
    Iterable<V> values();

    /*
    Returns true if true if size of the map equals zero and false if at least one entry is present in the map.
     */
    boolean isEmpty();

    /*
    Returns the number of entries found in the map.
     */
    int size();

}

