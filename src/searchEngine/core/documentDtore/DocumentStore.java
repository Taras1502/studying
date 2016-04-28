package searchEngine.core.documentDtore;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DocumentStore {
    private static final int INT_SIZE = 4;
    private static final int SHORT_SIZE = 2;

    private final String path;
    private int availableDocId;

    private Map<Integer, DocData> documents;
    private Map<Integer, List<Integer>> hashes;
    private RandomAccessFile docDataStore;
    private boolean updated;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final Object fileLock = new Object();

    private DocumentStore(String workingDir) {
        path = workingDir + "/" + "docStore";
        availableDocId = 0;
        updated = false;
        documents = new HashMap<>();
        hashes = new HashMap<>();
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
            System.out.println("FP " + docDataStore.getFilePointer());
            long pos = 0;
            int docId;
            docDataStore.seek(0);
            do {
                docId = docDataStore.readInt();
                int segId = docDataStore.readInt();
                int docHash = docDataStore.readInt();
                short pathLen = docDataStore.readShort();
                docDataStore.skipBytes(pathLen);

                DocData currentDoc = new DocData(segId, pos);
                documents.put(docId, currentDoc);

                // adding doc to the hash store
                List<Integer> docs = hashes.get(docHash);
                if (docs == null) {
                    docs = new ArrayList<>();
                    hashes.put(docHash, docs);
                }
                docs.add(docId);
                System.out.println("load " + docId + " " + segId + " " + docHash + " " + pathLen);
                pos += INT_SIZE + INT_SIZE + SHORT_SIZE + pathLen;
            } while (docDataStore.getFilePointer() < docsFileLen);
            documentStore.availableDocId = docId + 1;
            return documentStore;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static DocumentStore create(String path) {
        return new DocumentStore(path);
    }

    public int registerDocument(String docPath, int segmentId) {
        int id = contains(docPath);
        if (id != -1) {
            try {
                writeLock.lock();
                DocData docData = documents.get(id);
                if (docData != null) {
                    docData.setSegmentId(segmentId);
                    updated = true;
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
                return docId;
            } finally {
                writeLock.unlock();
            }
        }
        return -1;
    }

    private int contains(String docPath) {
        int fileHash = docPath.hashCode();
        List<Integer> docs;
        try {
            readLock.lock();
            docs = hashes.get(fileHash);
        } finally {
            readLock.unlock();
        }
        if (docs != null && !docs.isEmpty()) {
            for (int id : docs) {
                String p = getDocPath(id);
                if (docPath.equalsIgnoreCase(p)) {
                    return id;
                }
            }
        }
        return -1;
    }

    public void updateSegmentId(int docId, int segmentId) {
        try {
            writeLock.lock();
            DocData docData = documents.get(docId);
            if (docData != null) {
                docData.setSegmentId(segmentId);
                updated = true;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public int getSegmentId(int docId) {
        try {
            readLock.lock();
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

    public String getDocPath(int docId) {
        try {
            readLock.lock();

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
                    DocData docData = rec.getValue();
                    if (docData.isUpdated()) {
                        docDataStore.seek(docData.getPosition() + INT_SIZE);
                        docDataStore.writeInt(docData.getSegmentId());
                        // write lock should be used here but theoretically should use this flag
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
            docDataStore.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
