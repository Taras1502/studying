package searchEngine;

import javafx.geometry.Pos;

import java.nio.ByteBuffer;
import java.util.*;

/**
 * Created by Taras.Mykulyn on 13.04.2016.
 */
public class PostList {
    private int size;
    private Map<Integer, List<Integer>> posts;
    public PostList() {
        posts = new TreeMap<>();
        size = 0;
    }

    public static PostList fromBytes(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);

        PostList postList = new PostList();
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

    public void addPost(int id, int pos) {
        List<Integer> post = posts.get(id);
        if (post == null) {
            post = new ArrayList<>();
            posts.put(id, post);
            size += 8; // 4 bytes for docID and 4 bytes for num of positions
        }
        post.add(pos);
        size += 4; // 4 bytes for pos
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

    public PostList mergePostList(PostList that) {
        Map<Integer, String> res = new TreeMap<>();
        Set<Integer> thisDocIds = this.posts.keySet();
        Set<Integer> thatDocIds = that.posts.keySet();

        thisDocIds.addAll(thatDocIds);
        Iterator it = thisDocIds.iterator();
        while(it.hasNext()) {
            int docId = (int) it.next();
//            if (docRegistry.getRelevant(docId) == )
        }
        return null;
    }

//    private PostList merge(PostList p1, PostList p2) {
//
//    }



    private static List<Integer>  mergeLists(List<Integer> list1, List<Integer> list2) {
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
        return posts.toString();
    }


    public static void main(String[] args) {
        List<Integer> l1 = new ArrayList<>();
        List<Integer> l2 = new ArrayList<>();

        l1.addAll(Arrays.asList(1,2,5,4,7));
        l2.addAll(Arrays.asList(1,3,6));

        System.out.println(mergeLists(l1, l2).toString());
    }
}
