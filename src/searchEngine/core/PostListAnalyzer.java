package searchEngine.core;


import java.util.*;

/**
 * Created by macbookpro on 5/14/16.
 */
public class PostListAnalyzer {
    private searchEngine.core.documentStore.DocumentStore documentStore;

    public PostListAnalyzer(searchEngine.core.documentStore.DocumentStore documentStore) {
        this.documentStore = documentStore;
    }

    public void rank(List<PostList> postLists) {
        Map<Integer, Double> res = new HashMap<>();

        int docNum = 200; //documentStore.getDocNum();
        for(PostList postList: postLists) {
            int docFrequency = postList.getPosts().size();
            for (int i = 0; i < docFrequency; i++) {
                IntBuffer post = postList.getPosts().getByIndex(i);
                int docId = post.getByIndex(0);
                int termFrequency = post.size() - 1;
                Double score = res.get(docId);
                if (score == null) {
                    score = 0d;
                }
                score += termFrequency * calculateIDF(docNum, docFrequency);
                res.put(docId, score);
            }
        }
        List<Map.Entry<Integer, Double>> sortedRes = new ArrayList<>(res.entrySet());
        Collections.sort(sortedRes, new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        Map<Integer, Double> sortedMap = new LinkedHashMap<Integer, Double>();
        for (Map.Entry<Integer, Double> entry : sortedRes) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }
        System.out.println(sortedMap);
    }

    private double calculateIDF(int docNum, int docFrequency) {
        return Math.log(docNum / docFrequency);
    }

    public static void main(String[] args) {
        PostList p = new PostList(0);
        p.addPost(1, 9);
        p.addPost(2, 10);
        p.addPost(2, 11);

        List<PostList> posts = new ArrayList<>();
        posts.add(p);

        new PostListAnalyzer(null).rank(posts);
    }

}
