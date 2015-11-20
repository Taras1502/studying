package algorithms.dataStructures.map;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 26.10.2015.
 */
public class LinkedListMap<K, V> implements Map<K, V> {
    private Entry<K, V> head;
    private Entry<K, V> tail;
    private int size;

    public LinkedListMap() {

    }

    @Override
    public void put(K key, V value) {
        System.out.println("putting...");
        Entry<K, V> current = getEntryByKey(key);
        if (current != null) {
            current.value = value;
        } else if (head != null){
            current = new Entry<>(key, value);
            current.next = head;
            current.next.prev = current;
            head = current;
            size++;
        } else {
            head = new Entry<>(key, value);
            tail = head;
            size++;
        }
        print();
    }

    @Override
    public V get(K key) {
        Entry<K, V> entry = getEntryByKey(key);
        if (entry != null) {
            return entry.value;
        } else {
            return null;
        }
    }

    @Override
    public void remove(K key) {
        System.out.println("Removing...");
        if (key == null || size < 1) return;
        if (head.key.equals(key)) {
            if (head.next != null) {
                head.next.prev = null;
            }
            head = head.next;
            size--;
        } else if (tail.key.equals(key)) {
            if (tail.prev != null) {
                tail.prev.next = null;
            }
            tail = tail.prev;
            size--;
        } else {
            Entry<K, V> current = getEntryByKey(key);
            if (current != null && current.key.equals(key)) {
                current.prev.next = current.next;
                current.next.prev = current.prev;
                size--;
            }
        }
        print();
    }

    @Override
    public boolean containsKey(K key) {
        Entry<K, V> entry = getEntryByKey(key);
        return entry != null;
    }

    @Override
    public boolean containsValue(V value) {
        Entry<K, V> entry = getEntryByValue(value);
        return entry != null;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public Iterable<K> keys() {
        return new Iterable<K>() {
            @Override
            public Iterator<K> iterator() {
                return new Iterator<K>() {
                    Entry<K, V> current = head;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public K next() {
                        if (current != null) {
                            Entry<K, V> entry = current;
                            current = current.next;
                            return entry.key;
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
                    Entry<K, V> current = head;

                    @Override
                    public boolean hasNext() {
                        return current != null;
                    }

                    @Override
                    public V next() {
                        if (current != null) {
                            Entry<K, V> entry = current;
                            current = current.next;
                            return entry.value;
                        }
                        return null;
                    }
                };
            }
        };
    }

    private Entry<K, V> getEntryByKey(K key) {
        if (head != null) {
            Entry<K, V> current = head;
            while(current.next != null && !current.key.equals(key)) {
                current = current.next;
            }
            if (current.key.equals(key)) {
                return current;
            }
        }
        return null;
    }

    private Entry<K, V> getEntryByValue(V value) {
        if (head != null) {
            Entry<K, V> current = head;
            while(current.next != null && !current.value.equals(value)) {
                current = current.next;
            }
            if (current.value.equals(value)) {
                return current;
            }
        }
        return null;
    }

    private void print() {
        Entry<K, V> current = head;
        System.out.print("size: " + size);
        while(current != null) {
            System.out.print("[" + current.key + ": " + current.value + "] ");
            current = current.next;
        }
        System.out.println();
    }

    private class Entry<K, V> {
        protected K key;
        protected V value;
        protected Entry prev;
        protected Entry next;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
            this.next = null;
            this.prev = null;
        }
    }

    public static void main(String[] args) {
        Map<String, String> map = new LinkedListMap<>();
        map.put("1", "Taras");
        map.put("1", "Taras");
        System.out.println(map.get("1"));
        map.remove("1");
        System.out.println(map.get("1"));
        map.remove("2");
        System.out.println(map.get("1"));

        map.put("1", "Taras");
        map.put("0", "Igor");

        System.out.println(map.get("1"));
        map.remove("0");
        System.out.println(map.get("0"));


    }
}
