package searchEngine.core.segments;

import searchEngine.interfaces.*;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Created by macbookpro on 4/27/16.
 */
public class MemorySegment {
    private static final int INT_SIZE = 4;

    private String path;
    private int id;
    private long size;
    private Map<String, PostList> segmentDictionary;

    private boolean committed;
    private boolean searchable;
    private boolean writable;

    public MemorySegment(int id, String path) {
        this.id = id;
        this.path = path;
    }

    public int getId() {
        return id;
    }

    public long getSize() {
        return size;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public boolean isWritable() {
        return writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }

    public PostList getPostList(String token) {
        return segmentDictionary.get(token);
    }

    public void addPostList(String token, int docId, int pos) {
        PostList postList = segmentDictionary.get(token);
        if (postList == null) {
            postList = new PostListImpl();
            segmentDictionary.put(token, postList);
            size += INT_SIZE * 2; // int1 - docID, int2 - posNumber
        }
        postList.addPost(docId, pos);
        size += INT_SIZE; // pos
    }

    public DiscSegment writeToDisc(Dictionary dictionary) {
        BufferedOutputStream indOS;
        DataOutputStream indDOS = null;

        int pos = 0;
        try {
            indOS = new BufferedOutputStream(new FileOutputStream(path));
            indDOS = new DataOutputStream(indOS);

            for (Map.Entry<String, PostList> e: segmentDictionary.entrySet()) {
                byte[] postList = e.getValue().toBytes();

                indDOS.writeInt(postList.length); //size =
                indDOS.write(postList);

                dictionary.addToken(e.getKey(), id, pos);
                pos += postList.length + INT_SIZE;
            }
            return new DiscSegment(id, path);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                indDOS.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public boolean commit() {
        // not implemented yet...
        return false;
    }
}
