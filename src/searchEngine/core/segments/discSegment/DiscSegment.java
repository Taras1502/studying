package searchEngine.core.segments.discSegment;

import searchEngine.core.Logger;
import searchEngine.core.PostList;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
// TODO: Provide thread safety
public class DiscSegment implements Serializable {
    private transient static final int INT_SIZE = 4;
    private static final transient String DISC_SEGMENT_PATH = "%s/%s.disc";
    private int id;
    private String workingDir;
    private String segmentPath;
    private transient RandomAccessFile index;

    private volatile boolean searchable;
    private volatile boolean inMerge;

    private transient DataOutputStream buffer;
    private transient int bufferSize;

    private final transient Object fileLock = new Object();
    private final transient ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final transient ReentrantReadWriteLock.ReadLock readLock = lock.readLock();

    private DiscSegment(String workingDir) {
        this.workingDir = workingDir;
        searchable = false;
    }

    private DiscSegment(int id, String workingDir) {
        this.id = id;
        this.workingDir = workingDir;
        segmentPath = String.format(DISC_SEGMENT_PATH, workingDir, id);
        searchable = true;
        inMerge = false;
        try {
            index = new RandomAccessFile(segmentPath, "rw");
        } catch (FileNotFoundException e) {
            Logger.error(getClass(), "Could not create disc segment file with id " + id);
        }
    }

    public static DiscSegment create(int id, String workingDir) {
        return new DiscSegment(id, workingDir);
    }

    public static DiscSegment asBuffer(String workingDir) {
        DiscSegment discSegment = new DiscSegment(workingDir);
        discSegment.segmentPath = String.format(DISC_SEGMENT_PATH, workingDir, String.valueOf(System.currentTimeMillis())) + ".part";
        discSegment.createBuffer();
        return discSegment;
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

    public boolean isInMerge() {
        return inMerge;
    }

    public void setInMerge(boolean inMerge) {
        this.inMerge = inMerge;
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
        } catch (Exception e) {
            try {
                System.out.println(Files.size(Paths.get(segmentPath)));
                System.out.println("FILE " + index.length() + " " + index.getFilePointer());
                System.out.println("DIFF  " + id + " " + pos);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
        return null;
    }

    public void close() {
        try {
            synchronized (fileLock) {
                if (index != null) {
                    index.close();
                }
            }
        } catch (IOException e) {
            Logger.error(getClass(), "Failed to close disc segment file with id " + id);
        }
    }

    public void removeDiscFile() {
        try {
            synchronized (fileLock) {
                close();
                Files.deleteIfExists(Paths.get(segmentPath));
            }
        } catch (IOException e) {
            Logger.error(getClass(), "Failed to close disc segment file with id " + id);
        }
    }

    private void createBuffer() {
        try {
            BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(segmentPath));
            buffer = new DataOutputStream(bufferOS);
            bufferSize = 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DiscSegment closeBuffer(int segmentId) {
        try {
            buffer.close();
            File segFile = new File(segmentPath);
            String newFileName = String.format(DISC_SEGMENT_PATH, workingDir, segmentId);
            segFile.renameTo(new File(newFileName));

            DiscSegment newDiscSegment = DiscSegment.create(segmentId, workingDir);
            newDiscSegment.setSearchable(true);
            return newDiscSegment;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int appendBuffer(PostList postList) {
        try {
            synchronized (fileLock) {
//            System.out.println("append");
                int pos = bufferSize;
                byte[] bytes = postList.toBytes();
                buffer.writeInt(bytes.length); // postList size (int)
                buffer.write(bytes); // postList bytes
                bufferSize += bytes.length + INT_SIZE;
                return pos;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public boolean canMerge() {
        synchronized (fileLock) {
            try {
                return index.length() > 0 && inMerge;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
    }
}
