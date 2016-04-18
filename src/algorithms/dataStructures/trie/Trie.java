package algorithms.dataStructures.trie;

import javax.management.MXBean;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Taras.Mykulyn on 26.11.2015.
 */

public class Trie<V> {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";
    private static final int R = 26;

    private Node<V> root;
    private int size;

    public Trie() {
        root = new Node<>(null);
    }

    public void add(String key, V value) {
        root = add(root, key, value, 0);
    }

    private Node add(Node node, String key, V val, int pos) {
        if (key.length() <= pos) {
            if (node == null) {
                size++;
                return new Node(val);
            } else {
                if (node.value == null) size++;
                node.value = val;
            }
        } else {
            int index = ALPHABET.indexOf(key.charAt(pos));
            if (node != null) {
                node.next[index] = add(node.next[index], key, val, pos + 1);
            } else {
                Node<V> newNode = new Node(null);
                newNode.next[index] = add(newNode.next[index], key, val, pos + 1);
                return newNode;
            }
        }
        return node;
    }

    public V get(String key) {
        System.out.println("getting " + key);
        return get(root, key, 0);
    }

    private V get(Node node, String key, int pos) {
        if (key.length() <= pos) {
            return (V) node.value;
        } else {
            int index = ALPHABET.indexOf(key.charAt(pos));
            System.out.println(ALPHABET.charAt(index));
            if (node != null && node.next[index] != null) {
                return get(node.next[index], key, pos + 1);
            } else {
                return null;
            }
        }
    }

    public void delete(String key) {
        root = delete(root, key, 0);
    }

    private Node delete(Node node, String key, int pos) {
        int index;
        if (pos == key.length()) {
            if (node != null) {
                node.value = null;
                size--;
            }
        } else {
            index = ALPHABET.indexOf(key.charAt(pos));
            node.next[index] = delete(node.next[index], key, pos + 1);
        }
        if (node.value == null && node.nextNodeIsEmpty()) {
            return null;
        }
        return node;
    }

    public List<String> keys() {
        List<String> keys = new ArrayList<>(size);
        keys(root, keys, "");
        return keys;
    }

    public void keys(Node node, List keys, String key) {
        if (node != null) {
            if (node.value != null) {
                keys.add(key);
            }
            for (int i = 0; i < R; i++) {
                if (node.next[i] != null) {
                    keys(node.next[i], keys, key + ALPHABET.charAt(i));
                }
            }
        }
    }

    public int size() {
        return size;
    }

    public Map<String, V> collect() {
        Map<String, V> map = new java.util.HashMap<>(size);
        collect(map, root, "");
        return map;
    }

    private void collect(Map map, Node node, String key) {
        if (node.value != null) {
            map.put(key, node.value);
        }
        if (node.next != null) {
            for (int i = 0; i < ALPHABET.length(); i++) {
                if (node.next[i] != null) {
                    collect(map, node.next[i], key + ALPHABET.charAt(i));
                }
            }
        }
    }

    public void trace(Node node) {
        for (int i = 0; i < 26; i++) {
            if (node.next[i] != null) {
                System.out.println(ALPHABET.charAt(i));
                trace(node.next[i]);
            }
        }
    }

    private static class Node<T> {
        T value;
        Node<T>[] next;

        public Node(T value) {
            this.value = value;
            next = new Node[R];
        }

        boolean nextNodeIsEmpty() {
            for (Node n: next) {
                if (n != null) {
                    return false;
                }
            }
            return true;
        }
    }

    public static void main(String[] args) {
        Trie<Integer> trie = new Trie();
        trie.add("add", 2);
        trie.add("advert", 1);
        trie.delete("add");
        trie.trace(trie.root);

        System.out.println();

        System.out.println(trie.collect().toString());
        System.out.println(trie.keys().toString());

        System.out.println(trie.size());
    }
}
