package searchEngine.newStructure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookpro on 4/19/16.
 */
public class ApplicationContext {
    public static final String SEGMENT_EXT = ".seg";
    public static final String DICTIONARY_EXT = ".dic";
    public static final String DOC_STORE_EXT = ".dstr";
    private String workingDir;
    private long maxInMemorySegmentSize = 10240000; // 10 mb
    private double loadFactor = 0.8; // 80%

    private Map<Integer, DiscSegment> discSegments;
    private InMemorySegment inMemorySegment;
    private Map<String, List<String>> inMemoryDictionary;
    private DocumentStore documentStore;

    private ApplicationContext() {}

    private ApplicationContext(String workingDir) {
        this.workingDir = workingDir;
        discSegments = new HashMap<>();
        inMemorySegment = new InMemorySegment(0);
        inMemoryDictionary = new HashMap<>();
        documentStore = DocumentStore.load(workingDir + "\\" + DOC_STORE_EXT);
        try {
            if (!Files.exists(new File(workingDir).toPath())) {
                Files.createDirectory(new File(workingDir).toPath());
            }
        } catch (IOException e) {
            System.out.println("FAILED TO CREATE WORKING DIRECTORY");
            e.printStackTrace();
        }
    }

    public static ApplicationContext create(String workingDir) {
        return new ApplicationContext(workingDir);
    }

    public static ApplicationContext load(String path) {
        //TODO: load every member from files...
        return null;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public String formDiscSegmentPath(int id) {
        return workingDir + "/" + id + SEGMENT_EXT;
    }

    public void addNewDiscSegment(DiscSegment discSegment) {
        discSegments.put(discSegment.getId(), discSegment);
        System.out.println(discSegments.toString());
    }

    public long getMaxInMemorySegmentSize() {
        return maxInMemorySegmentSize;
    }

    public void setMaxInMemorySegmentSize(int maxInMemorySegmentSize) {
        this.maxInMemorySegmentSize = maxInMemorySegmentSize;
    }

    public double getLoadFactor() {
        return loadFactor;
    }

    public Map<Integer, DiscSegment> getDiscSegments() {
        return discSegments;
    }

    public void setDiscSegments(Map<Integer, DiscSegment> discSegments) {
        this.discSegments = discSegments;
    }

    public InMemorySegment getInMemorySegment() {
        return inMemorySegment;
    }

    public void setInMemorySegment(InMemorySegment inMemorySegment) {
        this.inMemorySegment = inMemorySegment;
    }

    public Map<String, List<String>> getInMemoryDictionary() {
        return inMemoryDictionary;
    }

    public void setInMemoryDictionary(Map<String, List<String>> inMemoryDictionary) {
        this.inMemoryDictionary = inMemoryDictionary;
    }

    public DocumentStore getDocumentStore() {
        return documentStore;
    }

    public void setDocumentStore(DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

}
