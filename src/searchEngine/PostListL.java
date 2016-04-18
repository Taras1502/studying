package searchEngine;

import java.util.Arrays;

/**
 * Created by Taras.Mykulyn on 07.04.2016.
 */
public class PostListL {
    private static final int DEFAULT_POST_LIST_LENGTH = 20;
    private int[] list;
    private int size;

    public PostListL() {
        this(DEFAULT_POST_LIST_LENGTH);
    }

    public PostListL(int length) {
        size = 0;
        list = new int[length];
    }

    public PostListL(int[] list) {
        this.list = list;
        size = list.length;
    }

    public PostListL and(PostListL that) {
        int thisLength = list.length;
        int thatLength = that.list.length;

        int[] res = new int[Math.min(thisLength, thatLength)];
        int thisPos = 0;
        int thatPos = 0;
        int resPos = 0;

        while (thisPos < thisLength && thatPos < thatLength) {
            if (list[thisPos] < that.list[thatPos]) {
                thisPos++;
            } else if (list[thisPos] > that.list[thatPos]) {
                thatPos++;
            } else {
                res[resPos++] = list[thisPos];
                thisPos++;
                thatPos++;
            }
        }
        return new PostListL (Arrays.copyOf(res, resPos + 1));
    }

    public PostListL andNot(PostListL that) {
        int thisLength = list.length;
        int thatLength = that.list.length;

        int[] res = new int[thisLength];
        int thisPos = 0;
        int thatPos = 0;
        int resPos = 0;

        while (thisPos < thisLength && thatPos < thatLength) {
            if (list[thisPos] < that.list[thatPos]) {
                res[resPos++] = list[thisPos++];
            } else if (list[thisPos] > that.list[thatPos]) {
                thatPos++;
            } else {
                thisPos++;
                thatPos++;
            }

            if (thatPos == thatLength) {
                int postToAdd = thisLength - thisPos;
                System.arraycopy(list, thisPos, res, resPos, postToAdd);
                resPos += postToAdd;
            }
        }
        return new PostListL(Arrays.copyOf(res, resPos));
    }

    public PostListL or(PostListL that) {
        int thisLength = list.length;
        int thatLength = that.list.length;

        int[] res = new int[thisLength + thatLength];
        int thisPos = 0;
        int thatPos = 0;
        int resPos = 0;

        while (thisPos < thisLength && thatPos < thatLength) {
            if (list[thisPos] < that.list[thatPos]) {
                res[resPos++] = list[thisPos++];
            } else if (list[thisPos] > that.list[thatPos]) {
                res[resPos++] = that.list[thatPos++];
            } else {
                res[resPos++] = list[thisPos];
                thisPos++;
                thatPos++;
            }
            if (thisPos == thisLength) {
                int postToAdd = thatLength - thatPos;
                System.arraycopy(that.list, thatPos, res, resPos, postToAdd);
                resPos += postToAdd;
            } else if (thatPos == thatLength) {
                int postToAdd = thisLength - thisPos;
                System.arraycopy(list, thisPos, res, resPos, postToAdd);
                resPos += postToAdd;
            }
        }

        return new PostListL(Arrays.copyOf(res, resPos));
    }

    public static void main(String[] args) {

        int[] arr1 = {2, 3, 6, 7, 12, 34, 55, 66, 77};
        int[] arr2 = {1, 3, 5, 7, 16, 54, 55, 77, 101, 107};
        PostListL p1 = new PostListL(arr1);
        PostListL p2 = new PostListL(arr2);

        System.out.println(Arrays.toString(p2.andNot(p1).list));
    }

}
