package searchEngine.core;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class Document {
    private int segmentId;
    private long position;
    private boolean updated;

    public Document() {
        updated = false;
    }

    public Document(int segmentId, long position) {
        this.segmentId = segmentId;
        this.position = position;
    }

    public int getSegmentId() {
        return segmentId;
    }

    public void setSegmentId(int segmentId) {
        this.segmentId = segmentId;
        updated = true;
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public boolean isUpdated() {
        return updated;
    }

    public void setUpdated(boolean updated) {
        this.updated = updated;
    }
}
