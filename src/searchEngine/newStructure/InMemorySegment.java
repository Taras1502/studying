package searchEngine.newStructure;

import searchEngine.ReadablePartition;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by macbookpro on 4/19/16.
 */
public class InMemorySegment {
    private static final int INT_SIZE = 4;

    private int id;
    private long size;
    private Map<String, PostList> segmentDictionary;

    private boolean committed;
    private boolean searchable;

    public InMemorySegment(int id) {
        this.id = id;
        size = 0;
        segmentDictionary = new HashMap<>();
    }

    public long getSize() {
        return size;
    }

    public int getId() {
        return id;
    }

    public boolean isCommitted() {
        return committed;
    }

    public void setCommitted(boolean committed) {
        this.committed = committed;
    }

    public boolean isSearchable() {
        return searchable;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public void addPostList(String token, int docId, int pos) {
        PostList postList = segmentDictionary.get(token);
        if (postList == null) {
            postList = new PostList();
            segmentDictionary.put(token, postList);
            size += INT_SIZE * 2; // int1 - docID, int2 - posNumber
        }
        postList.addPost(docId, pos);
        size += INT_SIZE; // pos
    }

    public PostList getPostList(String token) {
        return segmentDictionary.get(token);
    }

    public DiscSegment writeToDisk(Map<String, List<String>> dictionary, String path) {
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

                dictionary.putIfAbsent(e.getKey(), new ArrayList<>());
                dictionary.get(e.getKey()).add(id + " " + pos);
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
}
