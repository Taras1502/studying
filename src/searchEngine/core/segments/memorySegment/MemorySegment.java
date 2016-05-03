package searchEngine.core.segments.memorySegment;


import searchEngine.core.PostList;
import searchEngine.core.index.Index;
import searchEngine.core.segments.discSegment.DiscSegment;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
public class MemorySegment {
    private static final int INT_SIZE = 4;

    private String path;
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

    public MemorySegment(int id, String path) {
        this.id = id;
        this.path = path;
        segmentDictionary = new HashMap<>();
        searchable = true;
        writable = true;
        updated = false;
    }

    public static MemorySegment create(int segmentId, String path) {
        if (Files.notExists(Paths.get(path))) {
            try {
                Files.createFile(Paths.get(path));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new MemorySegment(segmentId, path);
    }

    public static MemorySegment load(int id, String path) {
        ObjectInputStream ois = null;
        try {
            MemorySegment memorySegment = new MemorySegment(id, path);
            ois = new ObjectInputStream(new FileInputStream(path));
            memorySegment.segmentDictionary = (Map<String, searchEngine.core.PostList>) ois.readObject();
            memorySegment.size = new File(path).length();
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
                postList = new PostList();
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

    public void createBuffer() {
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

    public DiscSegment writeToDisc(Index index) {
        BufferedOutputStream indOS;
        DataOutputStream indDOS = null;

        int pos = 0;
        try {
            DiscSegment discSegment = new DiscSegment(id, path);

            readLock.lock();
            indOS = new BufferedOutputStream(new FileOutputStream(path + "d"));
            indDOS = new DataOutputStream(indOS);

            for (Map.Entry<String, PostList> e: segmentDictionary.entrySet()) {
                byte[] postList = e.getValue().toBytes();

                indDOS.writeInt(postList.length); // postList size (int)
                indDOS.write(postList); // postList bytes

                index.addToken(e.getKey(), discSegment, pos);
                pos += postList.length + INT_SIZE;
            }
            return new DiscSegment(id, path);
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
