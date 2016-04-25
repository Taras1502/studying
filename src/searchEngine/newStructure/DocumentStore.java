package searchEngine.newStructure;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    private boolean committed;
    private boolean updated;

    private DocumentStore(String path, Map<Integer, Document> documents) {
        this.path = path;
        this.documents = documents;
        this.committed = true;
        this.updated = false;
        this.availableDocId = 0;
    }

    public static DocumentStore load(String path) {
        Map<Integer, Document> documents = new HashMap<>();
        RandomAccessFile docsFile = null;
        try {
            docsFile = new RandomAccessFile(path, "rw");
            long docsFileLen = docsFile.length();
            long pos = 0;
            do {
                int docId = docsFile.readInt();
                int segId = docsFile.readInt();
                short pathLen = docsFile.readShort();
                // for test
//                byte[] pathBytes = new byte[pathLen];
//                docsFile.readFully(pathBytes);
//                String docPath = new String(pathBytes);
                docsFile.seek(pathLen);

                Document currentDoc = new Document();
                currentDoc.setSegmentId(segId);
                currentDoc.setPosition(pos);
                documents.put(docId, currentDoc);
                pos += INT_SIZE + INT_SIZE + SHORT_SIZE + pathLen;
            } while (docsFileLen > docsFile.getFilePointer());
            return new DocumentStore(path, documents);
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
        return new DocumentStore(path, new HashMap<>());
    }

    public boolean isCommitted() {
        return committed;
    }

    public boolean isUpdated() {
        return updated;
    }

    public int registerDocument(String filePath, int segmentId) {
        int docId = availableDocId++;
        RandomAccessFile docsFile = null;
        try {
            docsFile = new RandomAccessFile(path, "rw");
            long docsFileSize = docsFile.length();

            docsFile.seek(docsFileSize);
            docsFile.writeInt(docId);
            docsFile.writeInt(segmentId);
            byte[] pathBytes = filePath.getBytes();
            docsFile.writeShort(pathBytes.length);
            docsFile.write(pathBytes);

            Document doc = new Document(segmentId, docsFileSize);
            documents.put(docId, doc);
            return docId;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            try {
                docsFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public boolean commit() {
        if (!updated) {
            return true;
        }

        RandomAccessFile docsFile = null;
        try {
            docsFile = new RandomAccessFile(path, "rw");

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
            try {
                if (docsFile != null) {
                    docsFile.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String toString() {
        return documents.toString();
    }
}
