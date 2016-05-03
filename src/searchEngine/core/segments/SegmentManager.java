package searchEngine.core.segments;

import searchEngine.core.dictionary.Dictionary;
import searchEngine.core.documentStore.DocumentStore;
import searchEngine.core.segments.memorySegment.MemorySegment;
import searchEngine.newStructure.DiscSegment;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class SegmentManager {
    private static final String DISC_SEGMENTS_META_FILE = "segments.disc";
    private static final String MEMORY_SEGMENTS_META_FILE = "segments.mem";

    private DocumentStore documentStore;
    private Dictionary dictionary;
    private Map<Integer, DiscSegment> discSegments;
    private Map<Integer, MemorySegment> memorySegments;
    private String workingDir;
    private int maxDiscSegments = 10;
    private long maxInMemorySegSize = 10240000; // 10 mb

    private SegmentManager(String workingDir, Dictionary dictionary, DocumentStore documentStore) {
        this.workingDir = workingDir;
        this.dictionary = dictionary;
        this.documentStore = documentStore;
    }

    public static SegmentManager load(String workingDir, Dictionary dictionary, DocumentStore documentStore) {
        SegmentManager segmentManager = new SegmentManager(workingDir, dictionary, documentStore);
        segmentManager.loadSegmentsData();
        return segmentManager;
    }

    public static SegmentManager create(String workingDir, Dictionary dictionary, DocumentStore documentStore) {
        SegmentManager segmentManager = new SegmentManager(workingDir, dictionary, documentStore);
        segmentManager.discSegments = new HashMap<>();
        segmentManager.memorySegments = new HashMap<>();
        return segmentManager;
    }

//    public MemorySegment getMemorySegment(long spaceNeeded) {
//        if (maxInMemorySegSize - memorySegment.getSize() < spaceNeeded) {
//            String path = workingDir + "/" + memorySegment.getId() + ".disc";
//            DiscSegment discSegment = memorySegment.writeToDisc(dictionary);
//            discSegments.put(discSegment.getId(), discSegment);
//
//            if (discSegments.size() > maxDiscSegments) {
//                // TODO: merge two least relevant segments
//            }
//
//            memorySegment = new MemorySegment(discSegment.getId() + 1, path);
//        }
//        return memorySegment;
//    }


    private void mergeDiscSegments(DiscSegment seg1, DiscSegment seg2) {
        DiscSegment discSegment = new DiscSegment(seg1.getId(), "");
        discSegment.setSearchable(false);


    }

    private void loadSegmentsData() {
        ObjectInputStream discOIS = null;
        ObjectInputStream memOIS = null;
        try {
            discOIS = new ObjectInputStream(new FileInputStream(workingDir + "/" + DISC_SEGMENTS_META_FILE));
            discSegments = (Map<Integer, searchEngine.newStructure.DiscSegment>) discOIS.readObject();

            memOIS = new ObjectInputStream(new FileInputStream(workingDir + "/" + DISC_SEGMENTS_META_FILE));
            discSegments = (Map<Integer, searchEngine.newStructure.DiscSegment>) memOIS.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                memOIS.close();
                discOIS.close();
            } catch (IOException e) {
                System.err.println("Failed to load disc segments.");
                e.printStackTrace();
            }
        }
    }



}
