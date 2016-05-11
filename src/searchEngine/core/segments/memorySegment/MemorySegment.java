package searchEngine.core.segments.memorySegment;


import searchEngine.core.Logger;
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

public class MemorySegment implements Serializable {
    private static final int MAX_SIZE = 7024000;
    private final String MEMORY_SEGMENT_PATH = "%s/%s.mem";
    private static final int INT_SIZE = 4;

    private String workingDir;
    private String segmentPath;
    private volatile int id;
    private volatile long size;
    private Map<String, PostList> segmentDictionary;

    private volatile boolean searchable;
    private volatile boolean writable;
    private volatile boolean closing;

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
        closing = false;
        size = 0;
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
        try {
            readLock.lock();
            return size;
        } finally {
            readLock.unlock();
        }
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

    public boolean addPostList(String token, int docId, int pos) {
        try {
            writeLock.lock();
            if (size > MAX_SIZE) {
                writable = false;
                return false;
            }
            PostList postList = segmentDictionary.get(token);
            if (postList == null) {
                postList = new PostList(id);
                segmentDictionary.put(token, postList);
                size += INT_SIZE * 2; // int1 - docID, int2 - posNumber
            }
            postList.addPost(docId, pos);
            size += INT_SIZE; // pos
//            System.out.println("Added pos " + pos + " for token " + token + " in doc " + docId);
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean addPostList(String token, PostList postList) {
        try {
            writeLock.lock();
            if (size > MAX_SIZE) {
                writable = false;
                return false;
            }

            segmentDictionary.put(token, postList);
            size += postList.getSize();
            return true;
        } finally {
            writeLock.unlock();
        }
    }

    public boolean isClosing() {
        try {
            readLock.lock();
        return closing;
        } finally {
            readLock.unlock();
        }
    }

    public void setClosing(boolean closing) {
        try {
            writeLock.lock();
        this.closing = closing;
        } finally {
            writeLock.unlock();
        }
    }

    public DiscSegment writeToDisc(Index index, int id) {
        BufferedOutputStream indOS;
        DataOutputStream indDOS = null;

        int pos = 0;
        try {
            DiscSegment discSegment = DiscSegment.create(id, workingDir);
            discSegment.setSearchable(false);

            readLock.lock();
            closing = true;
            indOS = new BufferedOutputStream(new FileOutputStream(discSegment.getSegmentPath()));
            indDOS = new DataOutputStream(indOS);

            for (Map.Entry<String, PostList> e: segmentDictionary.entrySet()) {
                byte[] postList = e.getValue().toBytes();

                indDOS.writeInt(postList.length); // postList size (int)
                indDOS.write(postList); // postList bytes

                index.addToken(e.getKey(), discSegment, pos);
//                System.out.println("writing postList to disc for token " + e.getKey() + " to pos " + pos);
                pos = pos + postList.length + INT_SIZE;
            }
            indDOS.flush();

            segmentDictionary.clear();
            discSegment.setSearchable(true);
            Logger.info(getClass(), "FINISHED WRITING MEMORY SEGMENT WITH ID " + id);
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
