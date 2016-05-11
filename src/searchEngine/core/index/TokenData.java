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
        for (int i = 0; i < size; i+=2) {
            if (buff[i] == segmentId) {
                buff[i + 1] = pos;
            }
        }
    }

    public void removeDataForSeg(int segmentId) {
        for (int i = 0; i < size && buff[i] <= segmentId; i+=2) {
            if (buff[i] == segmentId) {
                for (int j = i; j < size - 2; j++) {
                    buff[j] = buff[j + 2];
                }
                size -= 2;
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
