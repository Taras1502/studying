package searchEngine;

import javafx.collections.transformation.SortedList;
import java.io.*;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.nio.CharBuffer;
import java.util.*;

/**
 * Created by Taras.Mykulyn on 12.04.2016.
 */
public class Test {

    private static final String INDEXES_DIR = "/Users/macbookpro/Desktop/ind/";
    private static final String SOURCE_DIR = "/Users/macbookpro/Desktop/test/";
    private static final String INDEXES_PART_EXT = ".indpart";
    private static TreeSet<String> STOP_WORDS;

    private static HashMap<Integer, ReadablePartition> partitions = new HashMap();

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        STOP_WORDS = new TreeSet<>(Arrays.asList(
                "", "a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "getByIndex", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"
        ));

        Map<String, List<String>> dictionary = new HashMap<>();
//
//        File dir = new File(SOURCE_DIR);
//        File[] files = dir.listFiles();
//        int counter = 0;
//        long start = System.currentTimeMillis();
//        for (File f: files) {
//            System.out.println("Indexing " + f.getPath());
//            create(f.getPath(), counter++, dictionary);
//        }
//        long p1 = System.currentTimeMillis();
//        System.out.println("index time " + (p1 - start));
//
//        File partionsF = new File(INDEXES_DIR + "partitions");
//        ObjectOutputStream poos = new ObjectOutputStream(new FileOutputStream(partionsF));
//        poos.writeObject(partitions);
//        poos.close();
//
//        File indPartFileO = new File(INDEXES_DIR + "dictionary.dic");
//        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indPartFileO));
//        oos.writeObject(dictionary);
//        oos.close();
        long p2 = System.currentTimeMillis();
//        System.out.println("writing dictionary to disc " + (p2 - p1));

        ObjectInputStream pois = new ObjectInputStream(new FileInputStream(INDEXES_DIR + "partitions"));
        partitions = (HashMap<Integer, ReadablePartition>) pois.readObject();

        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(INDEXES_DIR + "dictionary.dic"));
        Map<String, List<String>> m = (Map<String, List<String>>) ois.readObject();
        long p3 = System.currentTimeMillis();
        System.out.println("reading dictionary from disc " + (p3 - p2));
        System.out.println("size of the dictionsry " + m.size());


        Scanner s = new Scanner(System.in);
        while (s.hasNext()) {
            long p31 = System.currentTimeMillis();

            String find = s.next();
            if (find.equals("exit")) {
                break;
            }
            List<String> res = m.get(find);
            if (res == null || res.isEmpty()) {
                System.out.println("not found");
            } else {
                for (String p : res) {
                    String[] elem = p.split(" ");
                    System.out.println(partitions.get(Integer.valueOf(elem[0])).getPostList(Integer.valueOf(elem[1])).toString());
                }
            }
            long p4 = System.currentTimeMillis();
            System.out.println("time for search " + (p4 - p31));

        }
//        String find1 = "taras";
//        List<String> res1 = m.getByIndex(find1);
//        for (String p: res1) {
//            String[] elem = p.split(" ");
//            System.out.println(partitions.getByIndex(Integer.valueOf(elem[0])).getPostList(Integer.valueOf(elem[1])).toString());
//        }
//        long p5 = System.currentTimeMillis();
//        System.out.println(p5 - p4);

    }


    public static void create(String file, int fileId, Map<String, List<String>> dictionary) throws IOException {
        Partition p = new Partition(INDEXES_DIR + fileId + INDEXES_PART_EXT);

        BufferedReader br = new BufferedReader(new FileReader(file));
        String l;
        String[] arr;
        int pos = 0;

        while((l = br.readLine()) != null) {
            arr = l.split("[ .,:;\"\'{}()-+<>]+?");
            for (String token: arr) {
                token = token.toLowerCase();
                if (STOP_WORDS.contains(token)) continue;

                p.addToken(token, fileId, pos++);
            }
        }
        br.close();

        ReadablePartition rp = p.writeToDisk(dictionary);
        partitions.put(rp.getId(), rp);

    }

    public static void createIndex(String file, int fileId) throws IOException, ClassNotFoundException {
//        Map<String, PostList> map = new TreeMap<>();
//
//        BufferedReader br = new BufferedReader(new FileReader(file));
//        String l;
//        String[] arr;
//        int pos = 0;
//
//        WritablePartition writablePartition = new WritablePartition(fileId, INDEXES_DIR);
//
//        while((l = br.readLine()) != null) {
//            arr = l.split("[\\s+.,:;\"\'{}()-+<>]+");
//            for (String token: arr) {
//                token = token.toLowerCase();
//                if (STOP_WORDS.contains(token)) continue;
//
//                writablePartition.addToken(token, fileId, pos);
////                PostList postList = map.getByIndex(token);
////                if (postList == null) {
////                    postList = new PostList();
////                    map.put(token, postList);
////                }
////                postList.addPos(pos++);
//            }
//        }
//        br.close();
//
//
//        // write indexes in String format
////        File indPartFile = new File(INDEXES_DIR + fileId + INDEXES_PART_EXT);
////        BufferedWriter bw = new BufferedWriter(new FileWriter(indPartFile));
////        for (Map.Entry<String, PostList> e: map.entrySet()) {
////            bw.write(e.getKey() + " " + e.getValue().toString() + "\n");
////        }
////        bw.close();
//
//        writablePartition.writeToDisk();
//
//        //write indexes in byte format
////        File indPartFile1 = new File(INDEXES_DIR + fileId + INDEXES_PART_EXT + "b");
////        BufferedWriter bw1 = new BufferedWriter(new FileWriter(indPartFile1));
////        for (Map.Entry<String, PostList> e: map.entrySet()) {
////            short strLen = (short) (e.getKey().length() * 2);
////            short len = (short) (strLen + e.getValue().);
////            bw.write(e.getKey() + " " + e.getValue().toString() + "\n");
////        }
////        bw.close();
//
//
//        // write indexes in Object format
////        File indPartFileO = new File(INDEXES_DIR + fileId + fileId + INDEXES_PART_EXT);
////        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(indPartFileO));
////        for (Map.Entry<String, PostList> e: map.entrySet()) {
////            e.getValue().sort();
////        }
////        oos.writeObject(map);
////        oos.close();
////        long p2 = System.currentTimeMillis();
//
////        ObjectInputStream ois = new ObjectInputStream(new FileInputStream(indPartFileO));
////        Map<String, PostList> m = (Map<String, PostList>) ois.readObject();
////        System.out.println(m.toString());

    }


    public static void merge() {

    }

    public void test(String file, int ind) {

    }

}
