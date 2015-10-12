package algorithms.dataStructures.stack;

import java.util.Iterator;

/**
 * Created by Taras.Mykulyn on 05.10.2015.
 */
public class CustomArrayStack<T> implements Stack<T>, Iterable<T> {
    private int stackCapacity;
    private int size;
    private T[] array;

    public CustomArrayStack() {
        @SuppressWarnings("unchecked")
        final T[] array = (T[]) new Object[stackCapacity = 10];
        this.array = array;
        size = 0;
    }

    public void push(T element) {
        if (element != null) {
            if (size == stackCapacity) {
                changeStackCapacity(stackCapacity + stackCapacity / 4);
            }
            array[size++] = element;
        }
    }

    public T pop() {
        if (size == 0) {
            return null;
        } else {
            T element = array[--size];
            array[size] = null;
            if ((stackCapacity - size) >= stackCapacity / 4) {
                changeStackCapacity(stackCapacity - stackCapacity / 4);
            }
            return element;
        }
    }

    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<T>() {
            private int currentPos = size - 1;
            @Override
            public boolean hasNext() {
                return currentPos >= 0;
            }

            @Override
            public T next() {
                return array[currentPos--];
            }
        };
    }

    private void changeStackCapacity(int newStackCapacity) {
        stackCapacity = newStackCapacity;
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[size];
        System.arraycopy(array, 0, temp, 0, size);
        array = (T[]) new Object[stackCapacity];
        System.arraycopy(temp, 0, array, 0, size);
    }

    public static void main(String[] args) {
        CustomArrayStack<String> stack = new CustomArrayStack<>();
        stack.push("1");
        stack.push("2");
        stack.push("3");
        stack.push("4");
        stack.push("5");
        stack.push("6");
        stack.push("7");
        stack.push("8");
        stack.push("9");
        stack.push("10");
        stack.push("11");
        stack.push("12");
        for (String s: stack) {
            System.out.println(s);
        }
    }

}
