package searchEngine.interfaces;

import searchEngine.newStructure.*;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public class PostListImpl implements PostList {
    @Override
    public void addPost(int docId, int tokenPos) {

    }

    @Override
    public PostList fromBytes(byte[] array) {
        return null;
    }

    @Override
    public byte[] toBytes() {
        return new byte[0];
    }

    @Override
    public PostList merge(PostList that) {
        return null;
    }
}
