package searchEngine.core.segments.discSegment;

import searchEngine.core.Logger;
import searchEngine.core.PostList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DiscSegment {
    private int id;
    private String path;
    private RandomAccessFile index;

    private volatile boolean searchable;

    private final Object fileLock = new Object();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();


    public DiscSegment(int id, String path) {
        this.id = id;
        this.path = path;
        this.searchable = false;
        try {
            index = new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            Logger.error(getClass(), "Could not create disc segment file with id " + id);
        }

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
            byte[] postList;
            synchronized (fileLock) {
                index.seek(pos);
                postList = new byte[index.readInt()];
                index.read(postList);
            }
            return PostList.fromBytes(postList);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            synchronized (fileLock) {
                index.close();
            }
        } catch (IOException e) {
            Logger.error(getClass(), "Failed to close disc segment file with id " + id);
        }
    }
}