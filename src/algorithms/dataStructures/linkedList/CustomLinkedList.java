package algorithms.dataStructures.linkedList;

import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 06.10.2015.
 */
public class CustomLinkedList<T> implements Iterable<T> {
    private Node head = null;
    private Node tail = null;
    private int size = 0;

    public void addFirst(T element) {
        Node first = new Node(element);
        if (head == null) {
            head = tail = first;
        } else {
            first.next = head;
            head.prev = first;
            head = first;
        }
        size++;
    }

    public void addLast(T element) {
        if (head == null) {
            head = new Node(element);
            tail = head;
        } else {
            Node last = new Node(element);
            last.prev = tail;
            tail.next = last;
            tail = last;
        }
        size++;
    }

    public void remove(T element) {
        Node current = head;
        if (current.element.equals(element)) {
            head = current.next;
            if (--size <= 1) {
                tail = head;
            }
            return;
        }
        while (current.next != null && !current.next.element.equals(element) ) {
            current = current.next;
        }
        if (current.next != null) {
            current.next = current.next.next;
            if (current.next == null) {
                tail = current;
            }
            size--;
        }
    }

    public void removeFirst() {
        if (head != null) {
            head = head.next;
            if (size-- <= 1) {
                tail = head;
            }
        }
    }

    public void removeLast() {
        if (head != null) {
            if (size > 1) {
                Node preLast = tail.prev;
                preLast.next = null;
                tail = preLast;
            } else {
                head = null;
                tail = null;
            }
            size--;
        }
    }

    public T get(int index) {
        int currentPos = 0;
        Node current = head;
        while(currentPos < index && current != null) {
            current = current.next;
            currentPos++;
        }
        if (current != null) {
            return current.element;
        } else {
            return null;
        }
    }

    public T getFirst() {
        if (head != null) {
            return head.element;
        }
        return null;
    }

    public T getLast() {
        if (tail != null) {
            return tail.element;
        }
        return null;
    }

    public int size() {
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            Node currentNode = head;
            @Override
            public boolean hasNext() {
                return currentNode != null;
            }

            @Override
            public T next() {
                if (currentNode != null) {
                    T returnEl = currentNode.element;
                    currentNode = currentNode.next;
                    return returnEl;
                }
                return null;
            }
        };
    }

    public Iterator<T> straightOrderIterator() {
        return new StraightOrderIterator<>();
    }

    public Iterator<T> reverseOrderIterator() {
        return new ReverseOrderIterator<>();
    }

    private class Node {
        private T element;
        private Node prev;
        private Node next;

        public Node(T element) {
            this.element = element;
            next = null;
            prev = null;
        }
    }

    private class StraightOrderIterator<K> implements Iterator<K> {
        Node currentNode = head;
        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public K next() {
            if (currentNode != null) {
                T returnEl = currentNode.element;
                currentNode = currentNode.next;
                return (K) returnEl;
            }
            return null;
        }
    }

    private class ReverseOrderIterator<K> implements Iterator<K> {
        Node currentNode = tail;
        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public K next() {
            if (currentNode != null) {
                T returnEl = currentNode.element;
                currentNode = currentNode.prev;
                return (K) returnEl;
            }
            return null;
        }
    }

    public static void main(String[] args) {
        CustomLinkedList<String> linkedList = new CustomLinkedList<>();

        String one = "1";
        linkedList.addLast(one);

        String two = "2";
        linkedList.addLast(two);
        linkedList.remove(two);

        System.out.println(linkedList.getLast());
    }
}
