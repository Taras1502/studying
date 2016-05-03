package searchEngine.core.index;

import searchEngine.core.Logger;
import searchEngine.core.PostList;
import searchEngine.core.documentStore.DocumentStore;
import searchEngine.core.segments.discSegment.DiscSegment;
import searchEngine.core.segments.memorySegment.MemorySegment;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 5/4/16.
 */
public class Index {
    private final String DISC_SEGMENTS_FILE_PATH;
    private final String MEM_SEGMENTS_FILE_PATH;
    private final String DICTIONARY_FILE_PATH;
    private final String DOC_STORE_FILE_PATH;
    private final String workingDir;

    private DocumentStore documentStore;
    private Map<String, Map<DiscSegment, Integer>> dictionary;
    private boolean updated;

    private Map<Integer, DiscSegment> discSegments;
    private Map<Integer, MemorySegment> memorySegments;

    // dictionary lockers
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    private Index(String workingDir) {
        this.workingDir = workingDir;
        DISC_SEGMENTS_FILE_PATH = workingDir + "\\" + "segments.disc";
        MEM_SEGMENTS_FILE_PATH = workingDir + "\\" + "segments.disc";
        DICTIONARY_FILE_PATH = workingDir + "\\" + "dictionary";
        DOC_STORE_FILE_PATH = workingDir + "\\" + "docStore";
        updated = false;
    }

    public static Index load(String workingDir) {
        Index index = new Index(workingDir);
        index.loadData();
        index.documentStore = DocumentStore.load(index.DOC_STORE_FILE_PATH);
        return index;
    }

    public static Index create(String workingDir) {
        Index index = new Index(workingDir);
        index.documentStore = DocumentStore.create(index.DOC_STORE_FILE_PATH);
        // TODO: implement working directory clean
        return index;
    }

    public void addToken(String token, DiscSegment discSegment, int pos) {
        try {
            writeLock.lock();
            Map<DiscSegment, Integer> res = dictionary.get(token);
            if (res == null) {
                res = new HashMap<>();
                dictionary.put(token, res);
                System.out.println("res == null");
            }
            res.put(discSegment, pos);
            updated = true;
        } finally {
            writeLock.unlock();
        }
    }

    public PostList getPostList(String token) {
        // TODO: implement post list retrieval from both memory and disc segments
        // TODO: merge post lists
        return null;
    }

    public MemorySegment getMemorySegment(long fileSize) {
        // TODO: logic to analyze if the memory segment has enough space for file
        // TODO: merge disc segments if necessary
        // TODO:
        return null;
    }

    private void mergeDiscSegments(DiscSegment seg1, DiscSegment seg2) {
        MemorySegment memorySegment = new MemorySegment(seg1.getId(), "");
        PostList postList1;
        PostList postList2;
        PostList res;
        int newPos;
        for (Map.Entry<String, Map<DiscSegment, Integer>> entry: dictionary.entrySet()) {
            res = null;
            Map<DiscSegment, Integer> tokenData = entry.getValue();
            Integer pos1 = tokenData.get(seg1);
            Integer pos2 = tokenData.get(seg2);
            if (pos1 != null) {
                postList1 = seg1.getPostList(pos1);
                if (pos2 != null) {
                    postList2 = seg2.getPostList(pos2);
                    res = postList1.mergePostList(postList2);
                } else {
                    postList1.synch(documentStore);
                }
            } else if (pos2 != null) {
                res = seg2.getPostList(pos2);
                res.synch(documentStore);
            }
            try {
                writeLock.lock();
                updated = true;
                newPos = memorySegment.appendBuffer(res);
                tokenData.put(seg1, newPos);
                tokenData.remove(seg2);
            } finally {
                writeLock.unlock();
            }
        }
    }

    /*
        This operation writes in-memory dictionary to disc by serialization of the dictionary object.
        Originally designed to be called after creating or merging disc segments.
    */
    private boolean commitDictionary() {
        Logger.info(getClass(), "Committing dictionary.");
        ObjectOutputStream ous = null;
        try {
            readLock.lock();
            if (!updated) {
                return true;
            }
            ous = new ObjectOutputStream(new FileOutputStream(DICTIONARY_FILE_PATH));
            ous.writeObject(dictionary);
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

    private boolean commitSegmentsData() {
        Logger.info(getClass(), "Committing segments metadata.");
        ObjectOutputStream memOUS = null;
        ObjectOutputStream discOUS = null;
        try {
            memOUS = new ObjectOutputStream(new FileOutputStream(MEM_SEGMENTS_FILE_PATH));
            memOUS.writeObject(memorySegments);

            discOUS = new ObjectOutputStream(new FileOutputStream(DISC_SEGMENTS_FILE_PATH));
            discOUS.writeObject(memorySegments);

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                 memOUS.close();
                 discOUS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadData() {
        ObjectInputStream discOIS = null;
        ObjectInputStream memOIS = null;
        ObjectInputStream dictionaryOIS = null;
        try {
            discOIS = new ObjectInputStream(new FileInputStream(DISC_SEGMENTS_FILE_PATH));
            discSegments = (Map<Integer, DiscSegment>) discOIS.readObject();

            memOIS = new ObjectInputStream(new FileInputStream(MEM_SEGMENTS_FILE_PATH));
            memorySegments = (Map<Integer, MemorySegment>) memOIS.readObject();

            dictionaryOIS = new ObjectInputStream(new FileInputStream(DICTIONARY_FILE_PATH));
            dictionary = (Map<String, Map<DiscSegment, Integer>>) dictionaryOIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                if (memOIS != null) {
                    memOIS.close();
                }
                if (discOIS != null) {
                    discOIS.close();
                }
                if (dictionaryOIS != null) {
                    dictionaryOIS.close();
                }
            } catch (IOException e) {
                System.err.println("Failed to load disc segments.");
                e.printStackTrace();
            }
        }
    }


}
