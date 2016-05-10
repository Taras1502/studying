package searchEngine.core.documentStore;

import searchEngine.core.IntBuffer;
import searchEngine.core.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DocumentStore {
    private final String path;
    private int lastUsedDocId;

    private Map<Integer, DocData> documents;
    private Map<Integer, IntBuffer> hashes;
    private RandomAccessFile docDataStore;
    private boolean updated;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
    private final Object fileLock = new Object();

    private DocumentStore(String filePath) {
        path = filePath;
        lastUsedDocId = 0;
        updated = false;
        documents = new HashMap<>();
        hashes = new HashMap<>();
        try {
            Path dataStorePath = Paths.get(path);
            if (!Files.exists(dataStorePath)) {
                Files.createFile(dataStorePath);
            }
            docDataStore = new RandomAccessFile(path + "paths", "rw");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static DocumentStore load(String path) {
        DocumentStore documentStore = new DocumentStore(path);

        ObjectInputStream ois;
        try {
            ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(path)));

            documentStore.documents = (Map<Integer, DocData>) ois.readObject();
            documentStore.hashes = (Map<Integer, IntBuffer>) ois.readObject();
            documentStore.lastUsedDocId = ois.readInt();

            Logger.info(DocumentStore.class, "Document store has been loaded.");
            return documentStore;
        } catch (Exception e) {
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
                    docData.resetSegmentId(segmentId);
                    updated = true;
                    Logger.info(getClass(), "Document already exists.. DocId: " + id);
                    return id;
                } else {
                    System.out.println(" No doc with id " + id);
                }
            } finally {
                writeLock.unlock();
            }
        } else {
            try {
                writeLock.lock();
                lastUsedDocId++;
                int docId = lastUsedDocId;
                int docHash = docPath.hashCode();
                long pos = writeDoc(docPath);
                if (pos == -1) {
                    System.err.println("Failed to write doc data for document " + docPath);
                }
                DocData docData = new DocData();
                docData.addSegmentId(segmentId);
                docData.setPosition(pos);
                documents.put(docId, docData);
                IntBuffer docIds = hashes.get(docHash);
                if (docIds == null) {
                    docIds = IntBuffer.allocate(2);
                    hashes.put(docHash, docIds);
                }
                docIds.add(docId);
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
            documents.remove(docId);
            String path = getDocPath(docId);
            if (path != null) {
                hashes.get(path.hashCode()).remove(docId);
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
        IntBuffer docs;
        try {
            readLock.lock();
            docs = hashes.get(fileHash);
            if (docs != null) {
                for (int i = 0; i < docs.size(); i++) {
                    String p = getDocPath(docs.get(i));
                    if (docPath.equalsIgnoreCase(p)) {
                        return docs.get(i);
                    }
                }
            }
        } finally {
            readLock.unlock();
        }
        return -1;
    }

    public void resetSegmentId(int docId, int segmentId) {
        try {
            writeLock.lock();
            DocData docData = documents.get(docId);
            if (docData != null) {
                docData.resetSegmentId(segmentId);
                updated = true;
            }
        } finally {
            writeLock.unlock();
        }
    }

    public void addSegmentId(int docId, int segid) {
        try {
            writeLock.lock();
            DocData docData = documents.get(docId);
            if (docData != null) {
                docData.addSegmentId(segid);
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
    public IntBuffer getSegmentId(int docId) {
        try {
            readLock.lock();
            DocData docData = documents.get(docId);
            if (docData != null) {
                return docData.getSegmentId();
            } else {
                return null;
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
            DocData docData = documents.get(docId);
            if (docData != null) {
                return readDocPath(docData.getPosition());
            } else {
                return null;
            }
        } finally {
            readLock.unlock();
        }
    }

    public boolean commit() {
        ObjectOutputStream ous;
        try {
            readLock.lock();

            if (!updated) {
                return true;
            }
            synchronized (fileLock) {
                ous = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(path)));

                ous.writeObject(documents);
                ous.writeObject(hashes);
                ous.writeInt(lastUsedDocId);
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
                docDataStore.seek(pos);
                short pathLen = docDataStore.readShort();
                byte[] pathBytes = new byte[pathLen];
                docDataStore.read(pathBytes);
                return new String(pathBytes);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private long writeDoc(String docPath) {
        synchronized (fileLock) {
            try {
                long pos = docDataStore.length();
                docDataStore.seek(pos);
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
