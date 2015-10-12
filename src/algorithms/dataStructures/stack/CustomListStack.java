package algorithms.dataStructures.stack;

import futureTest.algorithms.dataStructures.linkedList.CustomLinkedList;

import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public class CustomListStack<T> implements Stack<T>, Iterable<T> {
    private CustomLinkedList<T> stack = new CustomLinkedList<>();

    @Override
    public void push(T element) {
        stack.addFirst(element);
    }

    @Override
    public T pop() {
        T returnElement = stack.getFirst();
        if (returnElement != null) {
            stack.removeFirst();
            return returnElement;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return stack.size() == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return stack.reverseOrderIterator();
    }
}
