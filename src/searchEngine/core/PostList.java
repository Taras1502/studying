package searchEngine.core;

import searchEngine.core.documentStore.*;
import searchEngine.core.documentStore.DocumentStore;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * author: Taras.Mykulyn 4/19/16.
 * This class is not thread safe.
 */
// TODO: Think of thread safety issue in this class. Provide if needed.
public class PostList implements Serializable {
    private int segId;
    private int size;
    private Map<Integer, List<Integer>> posts;

    public PostList(int segId) {
        this.segId = segId;
        posts = new TreeMap<>();
        size = 0;
    }

    public static PostList fromBytes(byte[] bytes, int segId) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PostList postList = new PostList(segId);
        while (byteBuffer.hasRemaining()) {
            int docId = byteBuffer.getInt();
            int positionsNum = byteBuffer.getInt();
            List<Integer> positions = new ArrayList<>(positionsNum);
            for (int i = 0; i < positionsNum; i++) {
                int p = byteBuffer.getInt();
                positions.add(p);
            }
            postList.posts.put(docId, positions);
        }
        return postList;
    }

    public int getSegmentId() {
        return segId;
    }

    public int getSize() {
        return size;
    }

    public void addPost(int id, Integer pos) {
        List<Integer> post = posts.get(id);
        if (post == null) {
            post = new ArrayList<>();
            posts.put(id, post);
            size += 8; // 4 bytes for docID and 4 bytes for num of positions
        }
        post.add(pos);
        size += 4; // 4 bytes for pos
    }

    public void addPost(int id, List<Integer> pos) {
        List<Integer> post = posts.get(id);
        if (post == null) {
            post = new ArrayList<>();
            posts.put(id, post);
            size += 8; // 4 bytes for docID and 4 bytes for num of positions
        }
        post.addAll(pos);
        size += 4 * pos.size(); // 4 bytes for each pos
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        for (Map.Entry<Integer, List<Integer>> post: posts.entrySet()) {
            List<Integer> positions = post.getValue();
            Collections.sort(positions);

            byteBuffer.putInt(post.getKey()); // docID
            byteBuffer.putInt(positions.size()); // num of positions
            for (int pos: positions) {
                byteBuffer.putInt(pos); // position
            }
        }
        return byteBuffer.array();
    }

    public void synch(DocumentStore documentStore) {
        Iterator<Map.Entry<Integer, List<Integer>>> it = posts.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> entry;
        while(it.hasNext()) {
            entry = it.next();
            if (documentStore.getSegmentId(entry.getKey()) != getSegmentId()) {
                it.remove();
            }
        }
    }

    public PostList mergePostList(PostList that, DocumentStore documentStore, int newSegId) {
        synch(documentStore);
        that.synch(documentStore);

        PostList res = new PostList(newSegId);
        int thisDocId;
        int thatDocId;
        Iterator<Map.Entry<Integer, List<Integer>>> thisIt = posts.entrySet().iterator();
        Iterator<Map.Entry<Integer, List<Integer>>> thatIt = that.posts.entrySet().iterator();
        Map.Entry<Integer, List<Integer>> thisEntry = thisIt.next();
        Map.Entry<Integer, List<Integer>> thatEntry = thatIt.next();

        while(thisIt.hasNext() && thatIt.hasNext()) {
            thisDocId = thisEntry.getKey();
            thatDocId = thatEntry.getKey();

            if (thisDocId < thatDocId) {
                res.addPost(thisDocId, thisEntry.getValue());
                thisEntry = thisIt.next();
            } else if (thisDocId > thatDocId) {
                res.addPost(thatDocId, thatEntry.getValue());
                thatEntry = thatIt.next();
            } else {
                // not likely to happen
                res.addPost(thisDocId, mergeLists(thisEntry.getValue(), thatEntry.getValue()));
                thisEntry = thisIt.next();
                thatEntry = thatIt.next();
            }
        }

        Iterator<Map.Entry<Integer, List<Integer>>> remaining;
        if (thisIt.hasNext()) {
            remaining = thisIt;
        } else if (thatIt.hasNext()){
            remaining = thatIt;
        } else {
            return res;
        }

        Map.Entry<Integer, List<Integer>> remainingEntry;
        while(remaining.hasNext()) {
            remainingEntry = remaining.next();
            res.addPost(remainingEntry.getKey(), remainingEntry.getValue());
        }
        return res;
    }

    private static List<Integer> mergeLists(List<Integer> list1, List<Integer> list2) {
        if (list1.get(list1.size() - 1) > list2.get(list2.size() - 1)) {
            return merge(list1, list2);
        } else {
            return merge(list2, list1);
        }
    }

    private static List<Integer> merge(List<Integer> list1, List<Integer> list2) {
        List<Integer> res = new ArrayList<>();
        int list1pos = 0;
        int list2pos = 0;
        while (list2pos < list2.size()) {
            if (list1.get(list1pos) < list2.get(list2pos)) {
                res.add(list1.get(list1pos++));
            } else if (list1.get(list1pos) > list2.get(list2pos)) {
                res.add(list2.get(list2pos++));
            } else {
                res.add(list1.get(list1pos));
                list1pos++;
                list2pos++;
            }
        }

        res.addAll(list1.subList(list1pos, list1.size()));
        return res;
    }

    @Override
    public String toString() {
        return segId + ": " + posts.toString();
    }
}
