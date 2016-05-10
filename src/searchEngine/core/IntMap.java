package searchEngine.core;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by macbookpro on 5/8/16.
 */
public class IntMap implements Serializable {
    static final transient int DEFAULT_KEYS_START_SIZE = 5;

    private int size;
    private IntBuffer[] values;

    private IntMap(int capacity) {
        values = new IntBuffer[capacity];
        this.size = 0;
    }

    private IntMap(IntBuffer[] values) {
        this.values = values;
        size = values.length;
    }

    public static IntMap allocate() {
        return new IntMap(DEFAULT_KEYS_START_SIZE);
    }

    public static IntMap allocate(int capacity) {
        return new IntMap(capacity);
    }

    public static IntMap fromArray(IntBuffer[] values) {
        return new IntMap(values);
    }

    public void add(int docId, int pos) {
        int ind = getIndex(docId);
        if (ind != -1) {
            values[ind].add(pos);
        } else {

            if (size == values.length) {
                changeCapacity(size + size / 4 + 1);
            }
            values[size] = IntBuffer.allocate();
            values[size].add(docId);
            values[size++].add(pos);

        }
    }

    public void add(int docId, IntBuffer positions) {
        int ind = getIndex(docId);
        if (ind != -1) {
            values[ind].append(positions.toArr());
            return;
        }

        if (size == values.length) {
            changeCapacity(size + size / 4 + 1);
        }
        values[size] = IntBuffer.allocate(positions.size() + 1);
        values[size].add(docId);
        values[size++].append(positions.toArr());
    }

    public IntBuffer get(int docId) {
        int ind = getIndex(docId);
        if (ind != -1) {
            return values[ind];
        }
        return null;
    }

    public int getIndex(int docId) {
        for (int i = 0; i < size; i++) {
            if (values[i].get(0) == docId) {
//                System.out.println(docId + " found");
                return i;
            }
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


    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    public static void main(String[] args) {
        IntMap intMap = IntMap.allocate();

        intMap.add(1, 2);
        System.out.println(intMap.toString());

        intMap.add(1, 3);
        System.out.println(intMap.toString());

        intMap.add(0, 2);
        System.out.println(intMap.toString());
    }
}
