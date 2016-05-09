package searchEngine.core;

import java.io.Serializable;

/**
 * Created by macbookpro on 5/8/16.
 */
public class IntMap implements Serializable {
    static final transient int DEFAULT_KEYS_START_SIZE = 5;

    private int size;
    private IntBuffer[] values;

    private IntMap(int size) {
        values = new IntBuffer[this.size = size];
    }

    private IntMap(IntBuffer[] values) {
        this.values = values;
        size = values.length;
    }

    public static IntMap allocate() {
        return new IntMap(DEFAULT_KEYS_START_SIZE);
    }

    public static IntMap allocate(int size) {
        return new IntMap(size);
    }

    public static IntMap fromArray(IntBuffer[] values) {
        return new IntMap(values);
    }

    public void add(int docId, int pos) {
        int ind = getIndex(docId);
        if (ind != -1) {
            values[ind].add(pos);
            return;
        }

        if (size == values.length) {
            changeCapacity(size + size / 4 + 1);
        }
        values[size] = IntBuffer.allocate();
        values[size].add(docId);
        values[size++].add(pos);
    }

    public IntBuffer get(int docId) {
        int ind = getIndex(docId);
        if (ind != -1) {
            return values[ind];
        }
        return null;
    }

    public int getIndex(int docId) {
        int counter = 0;
        for (IntBuffer post: values) {
            if (post != null && post.get(0) == docId) {
                return counter;
            }
            counter++;
        }
        return -1;
    }

    public void remove(int index) {
        if (index != -1) {
            for (int i = index; i < size - 1; i++) {
                values[i] = values[i + 1];
            }
            size--;
            values[size] = null;

            if (size < values.length / 2) {
                changeCapacity((values.length / 4) * 3 + 1);
            }
        }
    }

    public IntBuffer[] toArr() {
        return values;
    }

    public int size() {
        return size;
    }

    private void changeCapacity(int newCapacity) {
        IntBuffer[] temp = new IntBuffer[newCapacity];
        System.arraycopy(values, 0, temp, 0, values.length);
        values = temp;
    }
}
