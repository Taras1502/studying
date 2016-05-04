package searchEngine.core.segments.memorySegment;


import searchEngine.core.PostList;
import searchEngine.core.index.Index;
import searchEngine.core.segments.discSegment.DiscSegment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */

// TODO: Provide thread safety
public class MemorySegment {
    private final String MEMORY_SEGMENT_PATH = "%s\\%s.mem";
    private static final int INT_SIZE = 4;

    private String workingDir;
    private String segmentPath;
    private volatile int id;
    private volatile long size;
    private Map<String, PostList> segmentDictionary;

    private DataOutputStream buffer;
    private int bufferSize;

    private volatile boolean searchable;
    private volatile boolean writable;
    private volatile boolean updated;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private MemorySegment(int id, String workingDir) {
        this.id = id;
        this.workingDir = workingDir;
        segmentPath = String.format(MEMORY_SEGMENT_PATH, workingDir, id);
        segmentDictionary = new HashMap<>();
        searchable = true;
        writable = true;
        updated = false;
    }

    public static MemorySegment create(int segmentId, String workingDir) {
        MemorySegment memorySegment = new MemorySegment(segmentId, workingDir);
        Path segPath = Paths.get(memorySegment.segmentPath);
        if (Files.notExists(segPath)) {
            try {
                Files.createFile(segPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return memorySegment;
    }

    public static MemorySegment load(int id, String workingDir) {
        ObjectInputStream ois = null;
        try {
            MemorySegment memorySegment = new MemorySegment(id, workingDir);
            ois = new ObjectInputStream(new FileInputStream(memorySegment.segmentPath));
            memorySegment.segmentDictionary = (Map<String, searchEngine.core.PostList>) ois.readObject();
            memorySegment.size = new File(memorySegment.segmentPath).length();
            return memorySegment;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public int getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public searchEngine.core.PostList getPostList(String token) {
        try {
            readLock.lock();
            return segmentDictionary.get(token);
        } finally {
            readLock.unlock();
        }
    }

    public void addPostList(String token, int docId, int pos) {
        try {
            writeLock.lock();

            PostList postList = segmentDictionary.get(token);
            if (postList == null) {
                postList = new PostList(id);
                segmentDictionary.put(token, postList);
                size += INT_SIZE * 2; // int1 - docID, int2 - posNumber
            }
            postList.addPost(docId, pos);
            size += INT_SIZE; // pos
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    public void addPostList(String token, PostList postList) {
        try {
            writeLock.lock();
            segmentDictionary.put(token, postList);
            size += postList.getSize();
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    public void createBuffer(String path) {
        try {
            BufferedOutputStream bufferOS = new BufferedOutputStream(new FileOutputStream(path));
            buffer = new DataOutputStream(bufferOS);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void closeBuffer() {
        try {
            buffer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int appendBuffer(PostList postList) {
        try {
            byte[] bytes = postList.toBytes();
            buffer.writeInt(bytes.length); // postList size (int)
            buffer.write(bytes); // postList bytes
            bufferSize += bytes.length + INT_SIZE;

            return bufferSize;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public DiscSegment writeToDisc(Index index, int id) {
        BufferedOutputStream indOS;
        DataOutputStream indDOS = null;

        setWritable(false);
        int pos = 0;
        try {
            DiscSegment discSegment = new DiscSegment(id, workingDir);

            readLock.lock();
            indOS = new BufferedOutputStream(new FileOutputStream(discSegment.getSegmentPath()));
            indDOS = new DataOutputStream(indOS);

            for (Map.Entry<String, PostList> e: segmentDictionary.entrySet()) {
                byte[] postList = e.getValue().toBytes();

                indDOS.writeInt(postList.length); // postList size (int)
                indDOS.write(postList); // postList bytes

                index.addToken(e.getKey(), discSegment, pos);
                pos += postList.length + INT_SIZE;
            }
            return discSegment;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                indDOS.close();
                readLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /*
    public boolean commit() {
        System.out.println("committing...");
        ObjectOutputStream ous = null;
        try {
            readLock.lock();

            if (!updated) {
                return true;
            }
            ous = new ObjectOutputStream(new FileOutputStream(path));
            ous.writeObject(segmentDictionary);
            updated = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (ous != null) {
                    ous.close();
                }
                readLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    **/
}
