package searchEngine.newStructure;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public enum SegmentEvents {
    FILE_INDEX(0),
    SEGMENT_COMMIT(1);

    SegmentEvents(int id) {
        this.id = id;
    }

    private int id;
}
