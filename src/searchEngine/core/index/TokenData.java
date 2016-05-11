package searchEngine.core.index;

import searchEngine.core.IntBuffer;

/**
 * Created by Taras.Mykulyn on 06.05.2016.
 */
public class TokenData extends IntBuffer {
    private TokenData(int capacity) {
        super(capacity);
    }

    private TokenData(int[] array) {
        super(array);
    }

    public static TokenData allocate() {
        return new TokenData(IntBuffer.DEFAULT_START_SIZE);
    }

    public int getPosition(int segmentId) {
        for (int i = 0; i < size; i+=2) {
            if (buff[i] == segmentId) {
                return buff[i + 1];
            }
        }
        return -1;
    }

    public void setPosition(int segmentId, int pos) {
        int index = getIndexForSegId(segmentId);
        if (index == -1) {
//            System.out.println("Set pos");
            append(segmentId, pos);
        } else {
//            System.out.println("Reset Pos");
            buff[index + 1] = pos;
        }
    }

    public int getIndexForSegId(int segmentId) {
        for (int i = 0; i < size; i += 2) {
            if (buff[i] == segmentId) {
                return i;
            }
        }
        return -1;
    }

    public void removeDataForSeg(int segmentId) {
        for (int i = 0; i < size; i+=2) {
            if (buff[i] == segmentId) {
                for (int j = i; j < size - 2; j++) {
                    buff[j] = buff[j + 2];
                }
                size -= 2;
                break;
            }
        }
    }

    public static void main(String[] args) {
        TokenData tokenData = TokenData.allocate();
        tokenData.append(0, 4);
        System.out.println(tokenData.getByIndex(0));

        System.out.println(tokenData.size() + tokenData.toString());
    }


}
