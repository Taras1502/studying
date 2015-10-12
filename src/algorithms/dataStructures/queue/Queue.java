package algorithms.dataStructures.queue;

/**
 * Created by Taras.Mykulyn on 07.10.2015.
 */
public interface Queue<T> {
    void enqueue(T element);
    T dequeue();
    int size();
}
