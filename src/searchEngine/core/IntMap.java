package searchEngine.core;

import java.io.Serializable;
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
            values[size].add(pos);
            size += 1;

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

    public IntBuffer getByIndex(int ind) {
        return values[ind];
    }

    public int getIndex(int docId) {
        for (int i = 0; i < size; i++) {
            if (values[i].getByIndex(0) == docId) {
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

    public IntMap merge(IntMap that) {
        IntMap res = IntMap.allocate(size);

        int thisPos = 0;
        int thatPos = 0;

        int thisDocId;
        int thatDocId;
        while(thisPos < size && thatPos < that.size) {
            thisDocId = values[thisPos].getByIndex(0);
            thatDocId = that.values[thatPos].getByIndex(0);

            if (thisDocId < thatDocId) {
                res.addEntry(values[thisPos++]);
            } else if (thisDocId > thatDocId) {
                res.addEntry(that.values[thatPos++]);
            } else {
                if (values[thisPos].getByIndex(1) < that.values[thatPos].getByIndex(1)) {
                    values[thisPos].append(that.values[thatPos].toArr(), 1, that.values[thatPos].size);
                    res.addEntry(values[thisPos]);
                } else if (values[thisPos].getByIndex(1) > that.values[thatPos].getByIndex(1)) {
                    that.values[thatPos].append(values[thisPos].toArr(), 1, values[thisPos].size);
                    res.addEntry(that.values[thatPos]);
                } else {
                    System.out.println("WAS NOT EXPECTING");
                }
                thisPos++;
                thatPos++;
            }
        }

        while (thisPos < size) {
            res.addEntry(values[thisPos++]);
        }

        while (thatPos < that.size) {
            res.addEntry(that.values[thatPos++]);
        }
        return res;
    }


    private void addEntry(IntBuffer entry) {
        if (size == values.length) {
            changeCapacity(size + size / 4 + 1);
        }
        values[size++] = entry;
    }

    private void changeCapacity(int newCapacity) {
        IntBuffer[] temp = new IntBuffer[newCapacity];
        System.arraycopy(values, 0, temp, 0, values.length);
        values = temp;
    }

    public IntMap positionalAnd(IntMap that) {
        IntMap res = IntMap.allocate();

        int thisPos = 0;
        int thatPos = 0;

        int thisDocId;
        int thatDocId;
        while(thisPos < size && thatPos < that.size) {
            thisDocId = values[thisPos].getByIndex(0);
            thatDocId = that.values[thatPos].getByIndex(0);

            if (thisDocId < thatDocId) {
                thisPos++;
            } else if (thisDocId > thatDocId) {
                thatPos++;
            } else {
                IntBuffer postsMerge = values[thisPos].positionalAnd(that.values[thatPos]);
                if (postsMerge != null) {
                    System.out.println("found pos for posAnd");
                    res.addEntry(postsMerge);
                }
                thisPos++;
                thatPos++;
            }
        }
        return res;
    }


    @Override
    public String toString() {
        return Arrays.toString(values);
    }

    public void print() {
        System.out.println("size " + size);
        for (int i = 0 ; i < size; i++) {
            for (int j = 0; j < values[i].size(); j++) {
                System.out.print(values[i].getByIndex(j) + " ");
            }
            System.out.println( );
        }
    }

    public static void main(String[] args) {
        IntMap intMap = IntMap.allocate();
        intMap.add(1, 2);
        intMap.add(1, 3);
        intMap.add(2, 2);
        intMap.add(2, 6);
        intMap.print();


        IntMap intMap1 = IntMap.allocate();
        intMap1.add(1, 4);
        intMap1.add(1, 5);
        intMap1.add(2, 7);
        intMap1.add(2, 9);
        intMap1.print();

        intMap.positionalAnd(intMap1).print();
    }
}
