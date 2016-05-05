package searchEngine.core.documentStore;

import searchEngine.core.Logger;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DocumentStore {
    private static final int REMOVED_DOC_ID = -1;
    private static final int INT_SIZE = 4;
    private static final int SHORT_SIZE = 2;

    private final String path;
    private int availableDocId;

    private Set<Integer> removedDocs;
    private Map<Integer, DocData> documents;
    private Map<Integer, List<Integer>> hashes;
    private RandomAccessFile docDataStore;
    private boolean updated;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final Object fileLock = new Object();

    private DocumentStore(String filePath) {
        path = filePath;
        availableDocId = 0;
        updated = false;
        documents = new HashMap<>();
        hashes = new HashMap<>();
        removedDocs = new TreeSet<>();
        try {
            Path dataStorePath = Paths.get(path);
            if (!Files.exists(dataStorePath)) {
                Files.createFile(dataStorePath);
            }
            docDataStore = new RandomAccessFile(path, "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DocumentStore load(String path) {
        DocumentStore documentStore = new DocumentStore(path);
        Map<Integer, DocData> documents = documentStore.documents;
        Map<Integer, List<Integer>> hashes = documentStore.hashes;
        RandomAccessFile docDataStore = documentStore.docDataStore;
        try {
            long docsFileLen = docDataStore.length();
            long pos = 0;
            int docId;
            docDataStore.seek(0);
            do {
                docId = docDataStore.readInt();
                int segId = docDataStore.readInt();
                int docHash = docDataStore.readInt();
                short pathLen = docDataStore.readShort();
                docDataStore.skipBytes(pathLen);

                // preventing from loading removed documents
                if (docId == REMOVED_DOC_ID) {
                    Logger.info(DocumentStore.class, "Skipping doc record with doc hash " + docHash + ".");
                    continue;
                }

                DocData currentDoc = new DocData(segId, pos);
                documents.put(docId, currentDoc);

                // adding doc to the hash store
                List<Integer> docs = hashes.putIfAbsent(docHash, new ArrayList<>());
                docs.add(docId);
                pos += INT_SIZE + INT_SIZE + INT_SIZE + SHORT_SIZE + pathLen;
                Logger.info(DocumentStore.class, "Document has been loaded with docId " + docId);

            } while (docDataStore.getFilePointer() < docsFileLen);
            documentStore.availableDocId = docId + 1;
            Logger.info(DocumentStore.class, "Document store has been loaded.");
            return documentStore;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DocumentStore create(String path) {
        return new DocumentStore(path);
    }

    /*
        Registers documents in the document store and returns docId.
        If the document already exists - its docId is returned.
    */
    public int registerDocument(String docPath, int segmentId) {
        int id = contains(docPath);
        if (id != -1) {
            try {
                writeLock.lock();
                DocData docData = documents.get(id);
                if (docData != null) {
                    docData.setSegmentId(segmentId);
                    updated = true;
                    Logger.info(getClass(), "Document already exists.. DocId: " + id);
                    return id;
                }
            } finally {
                writeLock.unlock();
            }
        } else {
            try {
                writeLock.lock();
                int docId = availableDocId++;
                int docHash = docPath.hashCode();
                long pos = writeDoc(docId, segmentId, docHash, docPath);
                if (pos == -1) {
                    System.err.println("Failed to write doc data for document " + docPath);
                }
                documents.put(docId, new DocData(segmentId, pos));
                List<Integer> docIds = hashes.get(docId);
                if (docIds == null) {
                    docIds = new ArrayList<>();
                    hashes.put(docId, docIds);
                }
                docIds.add(docHash);
                Logger.info(getClass(), "Document has been registered.. DocId: " + docId + " Path: " + docPath);
                return docId;
            } finally {
                writeLock.unlock();
            }
        }
        return -1;
    }

    public void removeDoc(int docId) {
        boolean removed = false;
        try {
            writeLock.lock();
            DocData docData = documents.get(docId);
            if (docData != null) {
                docData.setUpdated(true);
                updated = true;
                removedDocs.add(docId);
                removed = true;
            }
        } finally {
            writeLock.unlock();
        }
        if (removed) {
            Logger.info(getClass(), "Document has been removed.. DocId: " + docId);
        } else {
            Logger.info(getClass(), "Document has not been removed.. DocId: " + docId + " was not found.");
        }
    }

    private int contains(String docPath) {
        int fileHash = docPath.hashCode();
        List<Integer> docs;
        try {
            readLock.lock();
            docs = hashes.get(fileHash);

            if (docs != null && !docs.isEmpty()) {
                for (int id : docs) {
                    if (removedDocs.contains(id)) continue;

                    String p = getDocPath(id);
                    if (docPath.equalsIgnoreCase(p)) {
                        return id;
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
        return -1;
    }

    public void updateSegmentId(int docId, int segmentId) {
        try {
            writeLock.lock();
            if (removedDocs.contains(docId)) {
                Logger.warn(getClass(), "Document with id " + docId + " has been removed. No further changes will be done.");
                return;
            }
            DocData docData = documents.get(docId);
            if (docData != null) {
                docData.setSegmentId(segmentId);
                updated = true;
            }
        } finally {
            writeLock.unlock();
        }
    }

    /*
        Returns segmentId that contains the most recent index data of the document.
        Return -1 if the docId was not found or the document has been removed.
    */
    public int getSegmentId(int docId) {
        try {
            readLock.lock();
            if (removedDocs.contains(docId)) {
                Logger.warn(getClass(), "Document with id " + docId + " has been removed.");
                return -1;
            }
            DocData docData = documents.get(docId);
            if (docData != null) {
                return docData.getSegmentId();
            } else {
                return -1;
            }
        } finally {
            readLock.unlock();
        }
    }

    /*
        Returns document path by the docId or null
        if either the docId was not found or the document has been removed.
    */
    public String getDocPath(int docId) {
        try {
            readLock.lock();
            if (removedDocs.contains(docId)) {
                Logger.warn(getClass(), "Document with id " + docId + " has been removed.");
                return null;
            }
            DocData docData = documents.get(docId);
            if (docData != null) {
                System.out.println("docPath " + docId);
                return readDocPath(docData.getPosition());
            } else {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    public boolean commit() {
        try {
            readLock.lock();

            if (!updated) {
                return true;
            }
            synchronized (fileLock) {
                for (Map.Entry<Integer, DocData> rec : documents.entrySet()) {
                    int docId = rec.getKey();
                    DocData docData = rec.getValue();

                    // if the doc has been removed than docId is changed to REMOVED_DOC_ID (-1) in the doc store file
                    // to prevent loading this doc to documents next time
                    if (removedDocs.contains(docId)) {
                        docDataStore.seek(docData.getPosition());
                        docDataStore.writeInt(REMOVED_DOC_ID);
                        continue;
                    }

                    if (docData.isUpdated()) {
                        docDataStore.seek(docData.getPosition() + INT_SIZE);
                        docDataStore.writeInt(docData.getSegmentId());
                        // write lock should be used here but theoretically no one uses this flag
                        // so read lock is preferred to avoid full lock on doc store.
                        docData.setUpdated(false);
                    }
                }
            }
            updated = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            readLock.unlock();
        }
    }

    private String readDocPath(long pos) {
        synchronized (fileLock) {
            try {
                docDataStore.seek(pos + INT_SIZE + INT_SIZE + INT_SIZE); // docId + segId + docHash
                short pathLen = docDataStore.readShort();
                byte[] pathBytes = new byte[pathLen];
                System.out.println(pos + " pathLen " + pathLen);
                docDataStore.read(pathBytes);
                return new String(pathBytes);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private long writeDoc(int docId, int segId, int docHash, String docPath) {
        synchronized (fileLock) {
            try {
                long pos = docDataStore.length();
                docDataStore.seek(pos);
                docDataStore.writeInt(docId);
                docDataStore.writeInt(segId);
                docDataStore.writeInt(docHash);
                byte[] pathBytes = docPath.getBytes();
                docDataStore.writeShort(pathBytes.length);
                docDataStore.write(pathBytes);
                return pos;
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
        }
    }

    public void close() {
        try {
            if (updated) {
                commit();
            }
            docDataStore.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
