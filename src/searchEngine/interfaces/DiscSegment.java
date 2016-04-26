package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface DiscSegment {
    int getId();

    boolean isSearchable();

    void setSearchable(boolean searchable);

    PostList getPostList(long pos);

    DiscSegment merge(DiscSegment discSegment);
}
