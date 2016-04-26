package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public class DiscSegmentImpl implements DiscSegment {

    public DiscSegmentImpl(int id, String path) {
//        this.id = id;
//        this.path = path;
    }

    @Override
    public int getId() {
        return 0;
    }

    @Override
    public boolean isSearchable() {
        return false;
    }

    @Override
    public void setSearchable(boolean searchable) {

    }

    @Override
    public PostList getPostList(long pos) {
        return null;
    }

    @Override
    public DiscSegment merge(DiscSegment discSegment) {
        return null;
    }
}
