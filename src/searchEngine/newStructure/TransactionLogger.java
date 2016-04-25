package searchEngine.newStructure;

/**
 * Created by Taras.Mykulyn on 25.04.2016.
 */
public class TransactionLogger {
    private String path;

    public TransactionLogger(String path) {
        this.path = path;
    }

    public void log(SegmentEvents segmentEvent) {
        switch (segmentEvent) {
            case FILE_INDEX:

                break;
            case SEGMENT_COMMIT:

                break;
            default:
                System.out.println("Unsupported operation.");
        }
    }
}
