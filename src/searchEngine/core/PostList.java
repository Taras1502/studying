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
            for (int i = 0; i < posts.size(); i++) {
                IntBuffer post = posts.get(i);
                if (post != null) {
                    int di = post.get(0);
                    byteBuffer.putInt(di); // docId
                    byteBuffer.putInt(post.size() - 1); // pos num
                    for (int j = 1; j < post.size(); j++) {
                        byteBuffer.putInt(post.get(j)); // positions
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
            for (int i = 0; i < posts.size(); i++) {
                IntBuffer post = posts.get(i);
                if (post != null) {
                    if (!documentStore.getSegmentId(post.get(0)).contains(segId)){
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

            res.posts = posts.merge(that.posts);

        } finally {
            readLock.unlock();
        }

        return null;
    }

    @Override
    public String toString() {
        return segId + ": " + posts.toString();
    }
}
