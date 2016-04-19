package searchEngine.newStructure;


import java.util.*;

/**
 * Created by macbookpro on 4/19/16.
 */
public class DocumentStore {
    private final String path;
    private Map<Integer, Set<Integer>> documents;

    public DocumentStore(String path) {
        this.path = path;
        documents = new HashMap<>();
    }

    public int registerDocument(String path) {
        int docId = path.hashCode();
        documents.put(docId, new TreeSet<>());
        return docId;
    }

    public void addSegmentsForDoc(int docId, Integer... segmentIds) {
        Set<Integer> segments = documents.get(docId);
        if (segments == null) {
            segments = new TreeSet<>();
            documents.put(docId, segments);
        }
        Collections.addAll(segments, segmentIds);
    }

    public void updateSegmentsForDoc(int docId, Integer... segmentIds) {
        Set<Integer> segments = new TreeSet<>();
        documents.put(docId, segments);
        Collections.addAll(segments, segmentIds);
    }

    public Set<Integer> getSegmentsForDoc(int docId) {
        return documents.get(docId);
    }

    @Override
    public String toString() {
        return documents.toString();
    }
}
