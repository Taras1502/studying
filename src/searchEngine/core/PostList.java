package searchEngine.core;

import searchEngine.core.documentStore.DocumentStore;

import java.io.Serializable;
import java.nio.ByteBuffer;
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
                positions.add(p);
            }
            postList.size += 4 + 4 + positionsNum * 4;
//            System.out.println("fromBytes: " + docId + " " + positions.toString());
            postList.posts.add(docId, positions);
        }
        return postList;
    }

    public IntMap getPosts() {
        return posts;
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

    public void addPosts(IntMap posts) {
        this.posts = posts;
        for (int i = 0; i < posts.size(); i++) {
            size += 4 + 4 * posts.getByIndex(i).size();
        }
    }

    public byte[] toBytes() {
        ByteBuffer byteBuffer = ByteBuffer.allocate(size);
        try {
            writeLock.lock();
            int written = 0;
            for (int i = 0; i < posts.size(); i++) {
                IntBuffer post = posts.getByIndex(i);
                if (post != null) {
                    int di = post.getByIndex(0);
                    byteBuffer.putInt(di); // docId
                    byteBuffer.putInt(post.size() - 1); // pos num
//                    System.out.println(post.size() - 1 + " " + byteBuffer.limit());
                    for (int j = 1; j < post.size(); j++) {
                        byteBuffer.putInt(post.getByIndex(j)); // positions
                    }
                    written += 4 + 4 + 4 * (post.size() - 1);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            writeLock.unlock();
        }

        return byteBuffer.array();
    }

    public void synch(DocumentStore documentStore) {
        try {
            writeLock.lock();
            for (int i = 0; i < posts.size(); i++) {
                IntBuffer post = posts.get(i);
                if (post != null) {
                    IntBuffer segIds = documentStore.getSegmentId(post.getByIndex(0));
                    if (segIds != null && !segIds.contains(segId)){
                        posts.remove(i);
                    }
                }
            }
        } finally {
            writeLock.unlock();
        }
    }

    public PostList mergePostList(PostList that, DocumentStore documentStore, int newSegId) {
        synch(documentStore);
        that.synch(documentStore);
        PostList res = new PostList(newSegId);
        try {
            readLock.lock();

            res.addPosts(posts.merge(that.posts));
//        System.out.println("merging ");
            return res;

        } finally {
            readLock.unlock();
        }
    }

    public PostList positionalAnd(PostList that) {
        System.out.println("here");
        PostList res = new PostList(segId);
        res.addPosts(posts.positionalAnd(that.posts));
        return res;
    }

    @Override
    public String toString() {
        return posts.size() + " " + size + ": " + posts.toString();
    }
}
