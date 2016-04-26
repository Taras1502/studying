package searchEngine.interfaces;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface PostList {
    void addPost(int docId, int tokenPos);

    PostList fromBytes(byte[] array);

    byte[] toBytes();

    PostList merge(PostList that);
}
