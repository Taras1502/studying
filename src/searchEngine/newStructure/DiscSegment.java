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

    private boolean searchable;

    public DiscSegment(int id, String path) {
        this.id = id;
        this.path = path;
        this.searchable = true;
    }

    public int getId() {
        return id;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public PostList getPostList(int pos) {
        try {
            index = new RandomAccessFile(path, "rw");
            index.seek(pos);
            int sizeToRead = index.readInt();
            System.out.println("size to read: " + sizeToRead);
            byte[] postList = new byte[sizeToRead];
            index.read(postList);
            return PostList.fromBytes(postList);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                index.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
