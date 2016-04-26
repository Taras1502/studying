package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface MemorySegment {
    int getId();

    boolean isSearchable();

    void setSearchable(boolean searchable);

    boolean isWritable();

    void setWritable(boolean writable);

    PostList getPostList(String token);

    void addPostList(String token, int docId, int pos);

    DiscSegment writeToDisc(Dictionary dictionary);

    boolean commit();
}
