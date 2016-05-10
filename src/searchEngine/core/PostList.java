package searchEngine.core;

import searchEngine.core.documentStore.DocumentStore;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * author: Taras.Mykulyn 4/19/16.
 * This class is not thread safe.
 */
// TODO: Think of thread safety issue in this class. Provide if needed.
public class PostList implements Serializable {
    private static final int MIN_BUFF_SIZE = 5;
    private int segId;
    private int size;
    private IntMap posts;

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
    private final ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();

    public PostList(int segId) {
        this.segId = segId;
        posts = IntMap.allocate();
        size = 0;
    }

    public static PostList fromBytes(byte[] bytes, int segId) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PostList postList = new PostList(segId);
        while (byteBuffer.hasRemaining()) {
            int docId = byteBuffer.getInt();
            int positionsNum = byteBuffer.getInt();
            IntBuffer positions = IntBuffer.allocate(positionsNum);
            for (int i = 0; i < positionsNum; i++) {
                int p = byteBuffer.getInt();
//                System.out.println("pos " + p);
                positions.add(p);
            }
//            System.out.println("fromBytes: " + docId + " " + positions.toString());
            postList.posts.add(docId, positions);
        }
        return postList;
    }

    public int getSegmentId() {
        try {
            readLock.lock();
            return segId;
        } finally {
            readLock.unlock();
        }
    }

    public int getSize() {
        try {
            readLock.lock();
            return size;
        } finally {
            readLock.unlock();
        }
    }

    public void addPost(int docId, int pos) {
        try {

            writeLock.lock();
            IntBuffer post = posts.get(docId);
            if (post == null) {
                size += 8; // 4 bytes for docID and 4 bytes for num of positions
                posts.add(docId, pos);
            } else {
                post.add(pos);
            }

            size += 4; // 4 bytes for pos
        } finally {
            writeLock.unlock();
        }
    }

    public void addPost(int id, int[] pos) {
        try {
            writeLock.lock();
//            posts.put(id, IntBuffer.fromArray(pos));
            size += 8 + 4 * pos.length; // 4 bytes for each pos
        } finally {
            writeLock.unlock();
        }
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);

        try {
            writeLock.lock();
            for (IntBuffer post: posts.toArr()) {
                if (post != null) {
                    int di = post.get(0);
                    if (di > 1380) {
                        System.out.println("writing " + post.toString());
                    }
                    byteBuffer.putInt(di); // docId
                    byteBuffer.putInt(post.size() - 1); // pos num
                    for (int i = 1; i < post.size(); i++) {
                        byteBuffer.putInt(post.get(i)); // positions
                    }
                }
            }

        } finally {
            writeLock.unlock();
        }

        return byteBuffer.array();
    }

    public void synch(DocumentStore documentStore) {
        try {
            writeLock.lock();

            IntBuffer[] array = posts.toArr();
            IntBuffer post;
            int i = 0;
            while ((post = array[i]) != null) {
                if (!documentStore.getSegmentId(post.get(0)).contains(segId)) {
                    posts.remove(i);
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public PostList mergePostList(PostList that, DocumentStore documentStore, int newSegId) {
//        synch(documentStore);
//        that.synch(documentStore);
//
//        PostList res = new PostList(newSegId);
//        int thisDocId;
//        int thatDocId;
//        try {
//            readLock.lock();
//
//            Iterator<Map.Entry<Integer, IntBuffer>> thisIt = posts.entrySet().iterator();
//            Iterator<Map.Entry<Integer, IntBuffer>> thatIt = that.posts.entrySet().iterator();
//            Map.Entry<Integer, IntBuffer> thisEntry = thisIt.next();
//            Map.Entry<Integer, IntBuffer> thatEntry = thatIt.next();
//
//            while(thisIt.hasNext() && thatIt.hasNext()) {
//                thisDocId = thisEntry.getKey();
//                thatDocId = thatEntry.getKey();
//
//                if (thisDocId < thatDocId) {
//                    res.addPost(thisDocId, thisEntry.getValue().toArr());
//                    thisEntry = thisIt.next();
//                } else if (thisDocId > thatDocId) {
//                    res.addPost(thatDocId, thatEntry.getValue().toArr());
//                    thatEntry = thatIt.next();
//                } else {
//                    // not likely to happen
//                    res.addPost(thisDocId, mergeLists(thisEntry.getValue(), thatEntry.getValue()));
//                    thisEntry = thisIt.next();
//                    thatEntry = thatIt.next();
//                }
//            }
//
//            Iterator<Map.Entry<Integer, List<Integer>>> remaining;
//            if (thisIt.hasNext()) {
//                remaining = thisIt;
//            } else if (thatIt.hasNext()){
//                remaining = thatIt;
//            } else {
//                return res;
//            }
//
//            Map.Entry<Integer, List<Integer>> remainingEntry;
//            while(remaining.hasNext()) {
//                remainingEntry = remaining.next();
//                res.addPost(remainingEntry.getKey(), remainingEntry.getValue());
//            }
//
//        } finally {
//            readLock.unlock();
//        }

        return null;
    }

    private static IntBuffer mergeLists(IntBuffer list1, IntBuffer list2) {
        if (list1.get(list1.size() - 1) > list2.get(list2.size() - 1)) {
            return merge(list1, list2);
        } else {
            return merge(list2, list1);
        }
    }

    private static IntBuffer merge(IntBuffer list1, IntBuffer list2) {
//        List<Integer> res = new ArrayList<>();
//        int list1pos = 0;
//        int list2pos = 0;
//        while (list2pos < list2.size()) {
//            if (list1.get(list1pos) < list2.get(list2pos)) {
//                res.add(list1.get(list1pos++));
//            } else if (list1.get(list1pos) > list2.get(list2pos)) {
//                res.add(list2.get(list2pos++));
//            } else {
//                res.add(list1.get(lprivate IntMap posts;ist1pos));
//                list1pos++;
//                list2pos++;
//            }
//        }
//
//        res.addAll(list1.subArry(list1pos, list1.size()));
        return null;
    }

    @Override
    public String toString() {
        return segId + ": " + posts.toString();
    }
}
