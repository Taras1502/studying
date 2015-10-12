package algorithms.dataStructures.queue;

import futureTest.algorithms.dataStructures.linkedList.CustomLinkedList;

import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public class CustomListQueue<T> implements Queue<T>, Iterable<T> {
    private CustomLinkedList<T> queue = new CustomLinkedList<>();

    @Override
    public void enqueue(T element) {
        queue.addLast(element);
    }

    @Override
    public T dequeue() {
        T returnElement = queue.getFirst();
        if (returnElement != null) {
            queue.removeFirst();
            return returnElement;
        }
        return null;
    }

    @Override
    public int size() {
        return queue.size();
    }

    @Override
    public Iterator<T> iterator() {
        return queue.straightOrderIterator();
    }
}
