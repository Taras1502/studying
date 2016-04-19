package searchEngine.newStructure;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Created by macbookpro on 4/19/16.
 */
public class DiscSegment {
    private int id;
    private String path;
    private RandomAccessFile index;

    public DiscSegment(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public PostList getPostList(int pos) {
        try {
            index = new RandomAccessFile(path, "rw");
            index.seek(pos);
            int sizeToRead = index.readInt();
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
