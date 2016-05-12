package searchEngine.core;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 06.05.2016.
 */
public class IntBuffer implements Serializable {
    protected static transient final int DEFAULT_START_SIZE = 5;
    protected int size = 0;
    protected int[] buff;

    protected IntBuffer(int capacity) {
        buff = new int[capacity];
    }

    protected IntBuffer(int[] array) {
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

    public void append(int... elements) {
        append(elements, 0, elements.length);
    }

    public void append(int[] source, int start, int end) {
        int numOfElements = end - start;
        if (size + numOfElements >= buff.length) {
            changeCapacity(size + numOfElements + size / 4 + 1);
        }
        System.arraycopy(source, start, buff, size, numOfElements);
        size += numOfElements;
    }

    public int getByIndex(int ind) {
        if (ind < size) {
            return buff[ind];
        }
        return -1;
    }

    public boolean contains(int elem) {
        return getIndex(elem) != -1;
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


    public IntBuffer positionalAnd(IntBuffer that) {
        IntBuffer res = IntBuffer.allocate();
        res.append(buff[0]); // adding doc id

        int thisPos = 1;
        int thatPos = 1;

        int thisTokenPos;
        int thatTokenPos;

        while(thisPos < size && thatPos < that.size) {
            thisTokenPos = buff[thisPos];
            thatTokenPos = that.buff[thatPos];

            if (thisTokenPos < thatTokenPos) {
                thisPos++;
                if (thatTokenPos - thisTokenPos == 1) {
                    res.append(thisTokenPos);
                    thatPos++;
                }
            } else if (thisTokenPos > thatTokenPos) {
                thatPos++;
            }
        }
        if (res.size() > 1) {
            return res;
        } else {
            return null;
        }
    }
    @Override
    public String toString() {
        return Arrays.toString(buff);
    }

    public static void main(String[] args) {

        int[] a = new int[4];
        a[0] = 4;
        a[1] = 5;
        a[2] = 6;
        a[3] = 7;

        IntBuffer intBuffer = IntBuffer.allocate(2);
        System.out.println(intBuffer.toString());

        intBuffer.append(3, 4);
        System.out.println(intBuffer.toString());

        intBuffer.append(2, 0);
        System.out.println(intBuffer.toString());

        intBuffer.add(1);
        System.out.println(intBuffer.toString());

        intBuffer.append(a, 1, a.length);
        System.out.println(intBuffer.toString() + " " + intBuffer.size());


    }
}
