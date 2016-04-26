package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface SegmentManager {
    MemorySegment getMemorySegment(long spaceNeeded);

    PostList retrievePostLists(int discSegId, long pos);

    PostList retrievePostList(String token);
}
