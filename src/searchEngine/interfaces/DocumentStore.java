package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface DocumentStore {
    int addDocument(String docPath, int docSeg);

    int removeDoc(String docPath);

    boolean contains(String docPath);

    String getDocPath(int docId);

    int getRelevantSegmentId(int docId);

    void updateDocSegment(int docId, int segId);

    boolean commit();
}
