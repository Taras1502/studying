package searchEngine.core.segments.discSegment;

import searchEngine.core.Logger;
import searchEngine.core.PostList;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
// TODO: Provide thread safety
public class DiscSegment implements Serializable {
    private final String DISC_SEGMENT_PATH = "%s\\%s.disc";
    private int id;
    private String workingDir;
    private String segmentPath;
    private transient RandomAccessFile index;

    private volatile boolean searchable;

    private final transient Object fileLock = new Object();
    private final transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final transient ReentrantReadWriteLock.ReadLock readLock = lock.readLock();


    public DiscSegment(int id, String workingDir) {
        this.id = id;
        this.workingDir = workingDir;
        segmentPath = String.format(DISC_SEGMENT_PATH, workingDir, id);
        searchable = false;
        try {
            index = new RandomAccessFile(segmentPath, "rw");
        } catch (FileNotFoundException e) {
            Logger.error(getClass(), "Could not create disc segment file with id " + id);
        }

    }

    public String getSegmentPath() {
        return segmentPath;
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
            return PostList.fromBytes(postList, id);
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
