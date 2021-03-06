package searchEngine.newStructure;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class SegmentManager {
    private static final String DISC_SEGMENTS_FILE_NAME = "segments.disc";
    private static final String MEMORY_SEGMENTS_FILE_NAME = "segments.mem";

    private Map<Integer, DiscSegment> discSegments;
    private InMemorySegment inMemorySegment;
    private String workingDir;
    private int maxDiscSegments = 10;
    private long maxInMemorySegSize = 10240000; // 10 mb

    private SegmentManager(String workingDir) {
        this.workingDir = workingDir;
    }

    public static SegmentManager load(String workingDir) {
        SegmentManager segmentManager = new SegmentManager(workingDir);
        segmentManager.loadDiscSegmentsData();
        segmentManager.loadMemorySegmentData();
        return segmentManager;
    }

    public static SegmentManager create(String workingDir) {
        SegmentManager segmentManager = new SegmentManager(workingDir);
        segmentManager.discSegments = new HashMap<>();
        segmentManager.inMemorySegment = new InMemorySegment(0);
        return segmentManager;
    }

    private void loadDiscSegmentsData() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(workingDir + "/" + DISC_SEGMENTS_FILE_NAME));
            discSegments = (Map<Integer, DiscSegment>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                System.err.println("Failed to load disc segments.");
                e.printStackTrace();
            }
        }
    }

    private void loadMemorySegmentData() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(workingDir + "/" + MEMORY_SEGMENTS_FILE_NAME));
            inMemorySegment = (InMemorySegment) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load in-memory segment.");
            e.printStackTrace();
        } finally {
            try {
                ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public InMemorySegment getInMemorySegment(long fileSize, Dictionary dictionary) {
        if (maxInMemorySegSize - inMemorySegment.getSize() < fileSize) {
            String path = workingDir + "/" + inMemorySegment.getId() + ".disc";
            DiscSegment discSegment = inMemorySegment.writeToDisk(dictionary, path);
            discSegments.put(discSegment.getId(), discSegment);

            if (discSegments.size() > maxDiscSegments) {
                // TODO: merge two least relevant segments
            }

            inMemorySegment = new InMemorySegment(discSegment.getId() + 1);
        }
        return inMemorySegment;
    }

    public InMemorySegment getInMemorySegment() {
        return inMemorySegment;
    }

    public DiscSegment getDiscSegment(int id) {
        return discSegments.get(id);
    }

}
