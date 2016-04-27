package searchEngine.core.segments;

import searchEngine.interfaces.*;
import searchEngine.newStructure.Dictionary;
import searchEngine.newStructure.DiscSegment;
import searchEngine.newStructure.InMemorySegment;

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
    private MemorySegment memorySegment;
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
        segmentManager.memorySegment = new MemorySegment(0, workingDir);
        return segmentManager;
    }

    public MemorySegment getMemorySegment(long spaceNeeded) {
        if (maxInMemorySegSize - memorySegment.getSize() < spaceNeeded) {
            String path = workingDir + "/" + memorySegment.getId() + ".disc";
            DiscSegment discSegment = memorySegment.writeToDisc(dictionary);
            discSegments.put(discSegment.getId(), discSegment);

            if (discSegments.size() > maxDiscSegments) {
                // TODO: merge two least relevant segments
            }

            memorySegment = new MemorySegment(discSegment.getId() + 1, path);
        }
        return memorySegment;
    }

    PostList retrievePostLists(int discSegId, long pos);

    PostList retrievePostList(String token);


    private void loadDiscSegmentsData() {
        ObjectInputStream ois = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(workingDir + "/" + DISC_SEGMENTS_FILE_NAME));
            discSegments = (Map<Integer, searchEngine.newStructure.DiscSegment>) ois.readObject();
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
            memorySegment = (MemorySegment) ois.readObject();
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



}
