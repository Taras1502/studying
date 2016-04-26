package searchEngine.newStructure;


import java.io.*;
import java.util.*;

/**
 * Created by macbookpro on 4/19/16.
 */
public class DocumentStore {
    private static final int INT_SIZE = 4;
    private static final int SHORT_SIZE = 2;

    private final String path;
    private int availableDocId;

    private Map<Integer, Document> documents;
    private Map<Integer, List<Integer>> hashes;
    private boolean committed;
    private boolean updated;

    private DocumentStore(String path, Map<Integer, Document> documents, Map<Integer, List<Integer>> hashes) {
        this.path = path;
        this.documents = documents;
        this.hashes = hashes;
        this.committed = true;
        this.updated = false;
        this.availableDocId = 0;
    }

    public static DocumentStore load(String path) {
        Map<Integer, Document> documents = new HashMap<>();
        Map<Integer, List<Integer>> hashes = new HashMap<>();
        RandomAccessFile docsFile = null;
        try {
            docsFile = new RandomAccessFile(path, "rw");
            long docsFileLen = docsFile.length();
            long pos = 0;
            do {
                int docId = docsFile.readInt();
                int docHash = docsFile.readInt();
                int segId = docsFile.readInt();
                short pathLen = docsFile.readShort();
                docsFile.seek(pathLen);

                Document currentDoc = new Document(segId, pos);
                documents.put(docId, currentDoc);

                // adding doc to the hash store
                List<Integer> docs = hashes.get(docHash);
                if (docs == null) {
                    docs = new ArrayList<>();
                    hashes.put(docHash, docs);
                }
                docs.add(docId);

                pos += INT_SIZE + INT_SIZE + SHORT_SIZE + pathLen;
            } while (docsFileLen > docsFile.getFilePointer());
            return new DocumentStore(path, documents, hashes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                docsFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static DocumentStore create(String path) {
        return new DocumentStore(path, new HashMap<>(), new HashMap<>());
    }

    public boolean isCommitted() {
        return committed;
    }

    public boolean isUpdated() {
        return updated;
    }

    public int registerDocument(String filePath, int segmentId) {
        int docId;
        int fileHash = filePath.hashCode();
        RandomAccessFile docsFile = getDocsFile();

        try {
            List<Integer> docs = hashes.get(fileHash);
            if (docs != null && !docs.isEmpty()) {
                for (int id: docs) {
                    String p = getFilePath(id);
                    if (p.equalsIgnoreCase(filePath)) {
                        updateSegmentForDoc(id, segmentId);
                        return id;
                    }
                }
            } else {
                docs = new ArrayList<>();
                docs.add(fileHash);
            }

            docId = availableDocId++;
            Document doc = new Document(segmentId, docsFile.length());
            documents.put(docId, doc);
            hashes.put(docId, docs);
            writeDoc(docsFile, docId, segmentId, fileHash, filePath);
            return docId;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        } finally {
            closeDocsFile(docsFile);
        }
    }

    public void updateSegmentForDoc(int docId, int segmentId) {
        Document doc = documents.get(docId);
        doc.setSegmentId(segmentId);
        updated = true;
    }

    public int getSegmentForDoc(int docId) {
        return documents.get(docId).getSegmentId();
    }

    public String getFilePath(int id) {
        RandomAccessFile docsFile = getDocsFile();
        try {
            return getFilePath(docsFile, id);
        } finally {
            closeDocsFile(docsFile);
        }
    }

    private String getFilePath(RandomAccessFile store, int id) {
        Document doc = documents.get(id);
        if (doc != null) {
            return readDocPath(store, doc.getPosition());
        } else {
            return null;
        }
    }

    public boolean commit() {
        if (!updated) {
            return true;
        }
        RandomAccessFile docsFile = getDocsFile();
        try {
            for (Map.Entry<Integer, Document> rec: documents.entrySet()) {
                Document doc = rec.getValue();
                if (doc.isUpdated()) {
                    docsFile.seek(doc.getPosition() + INT_SIZE);
                    docsFile.writeInt(doc.getSegmentId());
                    doc.setUpdated(false);
                }
            }
            committed = true;
            updated = false;
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            closeDocsFile(docsFile);
        }
    }

    private String readDocPath(RandomAccessFile store, long pos) {
        try {
            store.seek(pos + 3 * INT_SIZE); // docId + docHash + segId
            short pathLen = store.readShort();
            byte[] pathBytes = new byte[pathLen];
            store.readFully(pathBytes);
            return new String(pathBytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void writeDoc(RandomAccessFile store, int docId, int segId, int docHash, String docPath) {
        try {
            store.seek(store.getFilePointer());
            store.writeInt(docId);
            store.writeInt(segId);
            store.writeInt(docHash);
            byte[] pathBytes = docPath.getBytes();
            store.writeShort(pathBytes.length);
            store.write(pathBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RandomAccessFile getDocsFile() {
        try {
            return new RandomAccessFile(path, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void closeDocsFile(RandomAccessFile docsFile) {
        try {
            if (docsFile != null) {
                docsFile.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return documents.toString();
    }

    public static void main(String[] args) {
        DocumentStore store = DocumentStore.create("D:/test");
    }
}
