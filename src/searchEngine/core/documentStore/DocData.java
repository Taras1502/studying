package searchEngine.core.documentStore;

import searchEngine.core.IntBuffer;

/**
 * Created by macbookpro on 4/27/16.
 */
public class DocData {
    private IntBuffer segmentId;
    private long position;
    private int tokenNum;

    public DocData() {
        segmentId = IntBuffer.allocate();
    }

    public DocData(IntBuffer segmentIds, long position) {
        this.segmentId = segmentIds;
        this.position = position;
    }

    public IntBuffer getSegmentId() {
        return segmentId;
    }

    public void resetSegmentId(int segmentId) {
        this.segmentId = IntBuffer.allocate();
        this.segmentId.add(segmentId);
    }

    public void addSegmentId(int segmentId) {
        this.segmentId.add(segmentId);
    }

    public long getPosition() {
        return position;
    }

    public void setPosition(long position) {
        this.position = position;
    }

    public int getTokenNum() {
        return tokenNum;
    }

    public void setTokenNum(int tokenNum) {
        this.tokenNum = tokenNum;
    }

}
