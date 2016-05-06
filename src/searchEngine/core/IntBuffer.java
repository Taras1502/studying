package searchEngine.core;

/**
 * Created by Taras.Mykulyn on 06.05.2016.
 */
public class IntBuffer {
    static final int PLUS = 15;
    static final int Default_START_SIZE = 15;
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
        return new IntBuffer(Default_START_SIZE);
    }

    public static IntBuffer allocate(int size) {
        return new IntBuffer(size);
    }

    public static IntBuffer fromArray(int[] array) {
        return new IntBuffer(array);
    }

    public void add(int elem) {
        if (size == buff.length) {
            int[] temp = new int[buff.length + 15];
            System.arraycopy(buff, 0, temp, 0, buff.length);
            buff = null;
            buff = temp;
        }
        buff[size++] = elem;
    }

    public int get(int ind) {
        if (ind < size) {
            return buff[ind];
        }
        return -1;
    }

    public int[] toArr() {
        return buff;
    }

    public int[] subArry() {

    }

    public int size() {
        return size;
    }
}
