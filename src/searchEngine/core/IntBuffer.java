package searchEngine.core;

import java.io.Serializable;

/**
 * Created by Taras.Mykulyn on 06.05.2016.
 */
public class IntBuffer implements Serializable {
    static transient final int DEFAULT_START_SIZE = 5;
    int size = 0;
    private int[] buff;

    private IntBuffer(int size) {
        buff = new int[size];
    }

    private IntBuffer(int[] array) {
        buff = array;
        size = array.length;
    }

    public static IntBuffer allocate() {
        return new IntBuffer(DEFAULT_START_SIZE);
    }

    public static IntBuffer allocate(int size) {
        return new IntBuffer(size);
    }

    public static IntBuffer fromArray(int[] array) {
        return new IntBuffer(array);
    }

    public void add(int elem) {
        if (size == buff.length) {
            changeCapacity(size + size / 4 + 1);
        }
        buff[size++] = elem;
    }

    public int get(int ind) {
        if (ind < size) {
            return buff[ind];
        }
        return -1;
    }

    public boolean contains(int elem) {
        return get(elem) != -1;
    }

    public int getIndex(int elem) {
        for (int i = 0; i < size; i++)  {
            if (buff[i] == elem) {
                return i;
            }
        }
        return -1;
    }

    public void remove(int elem) {
        int ind = getIndex(elem);
        if (ind != -1) {
            for (int i = ind; i < size - 1; i++) {
                buff[i] = buff[i + 1];
            }
            size--;

            if (size < buff.length / 2) {
                changeCapacity((buff.length / 4) * 3 + 1);
            }
        }
    }

    public int[] toArr() {
        return buff;
    }

    public int[] subArray() {
        return toArr();
    }

    public int size() {
        return size;
    }

    private void changeCapacity(int newCapacity) {
        int[] temp = new int[newCapacity];
        System.arraycopy(buff, 0, temp, 0, buff.length);
        buff = temp;
    }
}
