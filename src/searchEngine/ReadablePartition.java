package searchEngine;

import fitnesse.authentication.SecureResponder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Set;

/**
 * Created by Taras.Mykulyn on 13.04.2016.
 */
public class ReadablePartition implements Serializable {
    private int id;
    private String path;
    private RandomAccessFile index;

    public ReadablePartition(String path, int id) {
        this.path = path;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public PostList getPostList(int pos) {
        try {
            index = new RandomAccessFile(path, "rw");
            index.seek(pos);
            short sizeToRead = index.readShort();
            System.out.println("sizetoread" + sizeToRead);
            byte[] postList = new byte[sizeToRead];
            index.read(postList);
            return PostList.fromBytes(postList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
