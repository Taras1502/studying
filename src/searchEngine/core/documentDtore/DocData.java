package searchEngine.core.documentDtore;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DocData {
    private int segmentId;
    private long position;
    private boolean updated;

    public DocData() {
        updated = false;
    }

    public DocData(int segmentId, long position) {
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
