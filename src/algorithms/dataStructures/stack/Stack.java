package algorithms.dataStructures.stack;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public interface Stack<T> {
    void push(T element);
    T pop();
    boolean isEmpty();
}
