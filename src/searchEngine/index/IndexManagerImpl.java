package searchEngine.index;

import searchEngine.interfaces.*;

import java.io.File;
import java.util.concurrent.ExecutorService;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public class IndexManagerImpl implements IndexManager {
    private String workingDir;
    private DocumentStore documentStore;
    private SegmentManager segmentManager;
    private Dictionary dictionary;

    private ExecutorService executor;



    public static IndexManager create(String workingDir) {
        return null;
    }

    public static IndexManager load(String workingDir) {
        return null;
    }

    @Override
    public void indexFile(String path) {
        File file = new File(path);
        long freeSpaceNeeded = file.length() / 3;
        MemorySegment memorySegment = segmentManager.getMemorySegment(freeSpaceNeeded);

        // doc registration
        int docId = documentStore.addDocument(path, memorySegment.getId());

        IndexTask indexTask = new IndexTask(memorySegment, path, docId);
        executor.submit(indexTask);
        // TODO: register document

        // TODO: get memorySegment available to store index data

        // TODO: create task for indexing file

        // TODO:
    }

    @Override
    public void indexFiles(String dirPath, boolean recursively) {

    }

    @Override
    public SearchResult search(String... tokens) {
        return null;
    }

    @Override
    public void close() {

    }
}
