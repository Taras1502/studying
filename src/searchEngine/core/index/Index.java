package searchEngine.core.index;

import searchEngine.core.IntBuffer;
import searchEngine.core.Logger;
import searchEngine.core.PostList;
import searchEngine.core.documentStore.DocumentStore;
import searchEngine.core.segments.discSegment.DiscSegment;
import searchEngine.core.segments.memorySegment.MemorySegment;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 5/4/16.
 */
public class Index {
    private static final int THREAD_NUM = 2; // TODO: need to configure dynamically
    private static final long MAX_MEM_SEGMENT_SIZE = 1024000; // 1 mb
    private static final int MAX_DISC_SEG_NUM = 1000; // 10 mb

    private final String DISC_SEGMENTS_FILE_PATH;
    private final String MEM_SEGMENTS_FILE_PATH;
    private final String DICTIONARY_FILE_PATH;
    private final String DOC_STORE_FILE_PATH;
    private final String workingDir;

    private DocumentStore documentStore;
    private Map<String, IntBuffer> dictionary;
    private boolean updated;

    private Map<Integer, DiscSegment> discSegments;
    private Map<Integer, MemorySegment> memorySegments;
    private AtomicInteger lastIdTaken;

    private ExecutorService executorService;

    // dictionary lockers
    private final ReentrantReadWriteLock dicLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock dicReadLock = dicLock.readLock();
    private final ReentrantReadWriteLock.WriteLock dicWriteLock = dicLock.writeLock();
    // memory segments lockers
    private final ReentrantReadWriteLock memSegLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock memSegReadLock = memSegLock.readLock();
    private final ReentrantReadWriteLock.WriteLock memSegWriteLock = memSegLock.writeLock();
    // memory segments lockers
    private final ReentrantReadWriteLock discSegLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock discSegReadLock = discSegLock.readLock();
    private final ReentrantReadWriteLock.WriteLock discSegWriteLock = discSegLock.writeLock();


    private Index(String workingDir) {
        this.workingDir = workingDir;
        DISC_SEGMENTS_FILE_PATH = workingDir + "/" + "segments.disc";
        MEM_SEGMENTS_FILE_PATH = workingDir + "/" + "segments.mem";
        DICTIONARY_FILE_PATH = workingDir + "/" + "dictionary";
        DOC_STORE_FILE_PATH = workingDir + "/" + "docStore";
        discSegments = new TreeMap<>();
        memorySegments = new TreeMap<>();
        dictionary = new HashMap<>();

        updated = false;
        lastIdTaken = new AtomicInteger(0);

        executorService = Executors.newFixedThreadPool(THREAD_NUM);
    }

    public static Index load(String workingDir) {
        Index index = new Index(workingDir);
        index.loadData();
        index.documentStore = DocumentStore.load(index.DOC_STORE_FILE_PATH);
        for (int segId: index.memorySegments.keySet()) {
            index.lastIdTaken.set(segId); // TODO: think of better solution
        }
        return index;
    }

    public static Index create(String workingDir) {
        Index index = new Index(workingDir);
        index.documentStore = DocumentStore.create(index.DOC_STORE_FILE_PATH);
        index.createNewMemorySegment();
        // TODO: implement working directory clean
        return index;
    }

    public DocumentStore getDocumentStore() {
        return documentStore;
    }

    /**
     * Adds token to the in-memory dictionary with the corresponding disc segment metadata.
     */
    public void addToken(String token, DiscSegment discSegment, int pos) {
        try {
            dicWriteLock.lock();
            IntBuffer res = dictionary.get(token);
            if (res == null) {
                res = IntBuffer.allocate();
                dictionary.put(token, res);
            }
            res.add(discSegment.getId());
            res.add(pos);
            updated = true;
        } finally {
            dicWriteLock.unlock();
        }
    }

    public PostList getPostList(String token) {
        long start = System.currentTimeMillis();
        // getting post lists from memory segments
        List<PostList> postLists = new ArrayList<>();
        try {
            memSegReadLock.lock();
            for (Map.Entry<Integer, MemorySegment> entry: memorySegments.entrySet()) {
                MemorySegment memorySegment = entry.getValue();
                if (memorySegment.isSearchable()) {
                    PostList p = memorySegment.getPostList(token);
                    if (p != null) {
                        postLists.add(p);
                    }
                }
            }
        } finally {
            memSegReadLock.unlock();
        }

        // getting post lists from disc segments
        IntBuffer discRes;
        try {
            dicReadLock.lock();
            discRes = dictionary.get(token);
        } finally {
            dicReadLock.unlock();
        }

        try {
            discSegReadLock.lock();
            if (discRes != null) {
                System.out.println(discRes.toString());
                int segIdIndex = 0;
                int posIndex = 1;
                while(posIndex < discRes.size()) {
                    DiscSegment discSegment = discSegments.get(discRes.get(segIdIndex));
                    if (discSegment != null && discSegment.isSearchable()) {
                        PostList postList = discSegment.getPostList(discRes.get(posIndex));
                        postLists.add(postList);
                    }
                    segIdIndex += 2;
                    posIndex += 2;
                }
            }
        } finally {
            discSegReadLock.unlock();
        }
        System.out.println("res " + postLists.toString());

        postLists.clear();
        // TODO: implement an efficient mechanism of merging multiple post lists
        return null;
    }

    public Future<MemorySegment> getMemorySegment(String filePath) {
        Future<MemorySegment> futureSegment = new FutureMemorySegment(this, filePath);
        return futureSegment;
    }

    MemorySegment getMemSegment(String filePath) {
        // memory segment management logic
        try {
            memSegWriteLock.lock();
            for (Map.Entry<Integer, MemorySegment> entry: memorySegments.entrySet()) {
                final MemorySegment memorySegment = entry.getValue();
                if (memorySegment.isWritable()) {
                    // TODO: logic to determine if the memory segment has enough space needs to be revised
                    return memorySegment;
                } else if (!memorySegment.isClosing()) {
                    memorySegment.setWritable(false);
                    memorySegment.setClosing(true);
                    Logger.info(getClass(), "Submitting task to write memory segment with id " + memorySegment.getId());
                    executorService.submit(new Runnable() {
                        @Override
                        public void run() {

                            writeMemorySegment(memorySegment);
                            createNewMemorySegment();
                        }
                    });
                }
            }

        } finally {
            memSegWriteLock.unlock();
        }

        // disc segment merge logic
        try {
            discSegReadLock.lock();
            if (discSegments.size() >= MAX_DISC_SEG_NUM - 1) {
                Iterator<DiscSegment> discSegIt = discSegments.values().iterator();
                final DiscSegment discSeg1 = discSegIt.next();
                final DiscSegment discSeg2 = discSegIt.next();
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        mergeDiscSegments(discSeg1, discSeg2);
                    }
                });
                Logger.info(getClass(), "Submitting task to merge disc segments with ids " + discSeg1.getId() + " " + discSeg2.getId());
            }
        } finally {
            discSegReadLock.unlock();
        }

        return null;
    }

    private MemorySegment createNewMemorySegment() {
        // new memory segment creation logic
        MemorySegment newMemSegment = MemorySegment.create(lastIdTaken.incrementAndGet(), workingDir);
        try {
            memSegWriteLock.lock();
            memorySegments.put(newMemSegment.getId(), newMemSegment);
        } finally {
            memSegWriteLock.unlock();
        }
        Logger.info(getClass(), "Created new memory segment with id " + newMemSegment.getId());
        return newMemSegment;
    }

    private void writeMemorySegment(MemorySegment memorySegment) {
        DiscSegment discSegment = memorySegment.writeToDisc(this, memorySegment.getId());
        // adding newly created disc segment
        try {
            discSegWriteLock.lock();
            discSegments.put(discSegment.getId(), discSegment);
        } finally {
            discSegWriteLock.unlock();
        }
        // removing memory segment
        try {
            memSegWriteLock.lock();
            memorySegment.setSearchable(false);
            memorySegments.remove(memorySegment.getId());
        } finally {
            memSegWriteLock.unlock();
        }

    }

    private void mergeDiscSegments(DiscSegment seg1, DiscSegment seg2) {
        MemorySegment memorySegment = MemorySegment.create(lastIdTaken.incrementAndGet(), workingDir);
        memorySegment.setWritable(false);
        try {
            memSegWriteLock.lock();
            memorySegments.put(memorySegment.getId(), memorySegment);
        } finally {
            memSegWriteLock.unlock();
        }

        PostList postList1;
        PostList postList2;
        PostList res;
        for (Map.Entry<String, IntBuffer> entry: dictionary.entrySet()) {
            // disc segments merge logic
            res = null;
            IntBuffer tokenData = entry.getValue();
            int pos1 = tokenData.get(seg1.getId());
            Integer pos2 = tokenData.get(seg2.getId());
            if (pos1 != -1) {
                postList1 = seg1.getPostList(pos1);
                if (pos2 != null) {
                    postList2 = seg2.getPostList(pos2);
                    res = postList1.mergePostList(postList2, documentStore, seg1.getId());
                } else {
                    postList1.synch(documentStore);
                    res = postList1;
                }
            } else if (pos2 != null) {
                res = seg2.getPostList(pos2);
                res.synch(documentStore);
            }

            // new post list write logic
            memorySegment.addPostList(entry.getKey(), res);
            try {
                dicWriteLock.lock();
                updated = true;
//                tokenData.remove(seg1);
//                tokenData.remove(seg2);
            } finally {
                dicWriteLock.unlock();
            }
        }

        // new disc segment write logic
        writeMemorySegment(memorySegment);
    }

    /*
        This operation writes in-memory dictionary to disc by serializing dictionary object.
        Originally designed to be called after creating or merging disc segments.
    */
    private boolean commitDictionary() {
        Logger.info(getClass(), "Committing dictionary.");
        ObjectOutputStream ous = null;
        try {
            dicReadLock.lock();
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
                dicReadLock.unlock();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*
        This operation writes segment maps to disc by serializing map objects.
        Originally designed to be called after creating or merging disc segments. ???
    */
    private boolean commitSegmentsData() {
        Logger.info(getClass(), "Committing segments metadata.");
        ObjectOutputStream memOUS = null;
        ObjectOutputStream discOUS = null;
        try {
            memSegReadLock.lock();
            memOUS = new ObjectOutputStream(new FileOutputStream(MEM_SEGMENTS_FILE_PATH));
            for (MemorySegment s: memorySegments.values()) {
                memOUS.writeObject(s);
            }

//            memOUS.writeObject(memorySegments);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                memOUS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            memSegReadLock.unlock();
        }

        try {
            discSegReadLock.lock();
            discOUS = new ObjectOutputStream(new FileOutputStream(DISC_SEGMENTS_FILE_PATH));
            discOUS.writeObject(discSegments);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                 discOUS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            discSegReadLock.unlock();
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
            dictionary = (Map<String, IntBuffer>) dictionaryOIS.readObject();
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
