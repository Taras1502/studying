package searchEngine.newStructure;

import searchEngine.Partition;
import searchEngine.ReadablePartition;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Created by macbookpro on 4/19/16.
 */
public class IndexManager {
    private ApplicationContext appContext;
    private static Set<String> STOP_WORDS = new TreeSet<>(Arrays.asList(
            "", "a", "about", "above", "above", "across", "after", "afterwards", "again", "against", "all", "almost", "alone", "along", "already", "also","although","always","am","among", "amongst", "amoungst", "amount",  "an", "and", "another", "any","anyhow","anyone","anything","anyway", "anywhere", "are", "around", "as",  "at", "back","be","became", "because","become","becomes", "becoming", "been", "before", "beforehand", "behind", "being", "below", "beside", "besides", "between", "beyond", "bill", "both", "bottom","but", "by", "call", "can", "cannot", "cant", "co", "con", "could", "couldnt", "cry", "de", "describe", "detail", "do", "done", "down", "due", "during", "each", "eg", "eight", "either", "eleven","else", "elsewhere", "empty", "enough", "etc", "even", "ever", "every", "everyone", "everything", "everywhere", "except", "few", "fifteen", "fify", "fill", "find", "fire", "first", "five", "for", "former", "formerly", "forty", "found", "four", "from", "front", "full", "further", "get", "give", "go", "had", "has", "hasnt", "have", "he", "hence", "her", "here", "hereafter", "hereby", "herein", "hereupon", "hers", "herself", "him", "himself", "his", "how", "however", "hundred", "ie", "if", "in", "inc", "indeed", "interest", "into", "is", "it", "its", "itself", "keep", "last", "latter", "latterly", "least", "less", "ltd", "made", "many", "may", "me", "meanwhile", "might", "mill", "mine", "more", "moreover", "most", "mostly", "move", "much", "must", "my", "myself", "name", "namely", "neither", "never", "nevertheless", "next", "nine", "no", "nobody", "none", "noone", "nor", "not", "nothing", "now", "nowhere", "of", "off", "often", "on", "once", "one", "only", "onto", "or", "other", "others", "otherwise", "our", "ours", "ourselves", "out", "over", "own","part", "per", "perhaps", "please", "put", "rather", "re", "same", "see", "seem", "seemed", "seeming", "seems", "serious", "several", "she", "should", "show", "side", "since", "sincere", "six", "sixty", "so", "some", "somehow", "someone", "something", "sometime", "sometimes", "somewhere", "still", "such", "system", "take", "ten", "than", "that", "the", "their", "them", "themselves", "then", "thence", "there", "thereafter", "thereby", "therefore", "therein", "thereupon", "these", "they", "thickv", "thin", "third", "this", "those", "though", "three", "through", "throughout", "thru", "thus", "to", "together", "too", "top", "toward", "towards", "twelve", "twenty", "two", "un", "under", "until", "up", "upon", "us", "very", "via", "was", "we", "well", "were", "what", "whatever", "when", "whence", "whenever", "where", "whereafter", "whereas", "whereby", "wherein", "whereupon", "wherever", "whether", "which", "while", "whither", "who", "whoever", "whole", "whom", "whose", "why", "will", "with", "within", "without", "would", "yet", "you", "your", "yours", "yourself", "yourselves", "the"
    ));

    private String workingDir;
    private SegmentManager segmentManager;
    private Dictionary inMemoryDictionary;
    private DocumentStore documentStore;

    public static IndexManager create(String workingDir) {
        IndexManager indexManager = new IndexManager();
        indexManager.workingDir = workingDir;
        try {
            if (!Files.exists(new File(workingDir).toPath())) {
                Files.createDirectory(new File(workingDir).toPath());
            }
        } catch (IOException e) {
            System.err.println("FAILED TO CREATE WORKING DIRECTORY");
            return null;
        }
        indexManager.segmentManager = SegmentManager.create(workingDir);
        indexManager.inMemoryDictionary = Dictionary.create(workingDir + "\\dictionary");
        indexManager.documentStore = DocumentStore.create(workingDir + "\\docStore");
        return indexManager;
    }

    public IndexManager load(String workingDir) {
        IndexManager indexManager = new IndexManager();
        indexManager.workingDir = workingDir;

        indexManager.segmentManager = SegmentManager.load(workingDir);
        indexManager.inMemoryDictionary = Dictionary.load(workingDir + "\\dictionary");
        indexManager.documentStore = DocumentStore.load(workingDir + "\\docStore");
        return indexManager;
    }

    public void addFileToIndex(String path) {
        File file = new File(path);
        int docId = 0;//appContext.getDocumentStore().registerDocument(path);

        InMemorySegment inMemorySegment = appContext.getInMemorySegment();
        long freeSpace = appContext.getMaxInMemorySegmentSize() - inMemorySegment.getSize();
        if (file.length() / 2 > freeSpace) {
            int segId = inMemorySegment.getId();
            DiscSegment discSegment = inMemorySegment.writeToDisk(appContext.getInMemoryDictionary(),
                    appContext.formDiscSegmentPath(segId));
            appContext.addNewDiscSegment(discSegment);
            //creating new inMemory segment
            inMemorySegment = new InMemorySegment(segId + 1);
            appContext.setInMemorySegment(inMemorySegment);
        }

        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(path));
            String l;
            String[] arr;
            int pos = 0;

            while((l = br.readLine()) != null) {
                arr = l.split("[ .,:;\"\'{}()-+<>]+?");
                for (String token: arr) {
                    token = token.toLowerCase();
                    if (STOP_WORDS.contains(token)) continue;
                    // TODO: implement token filter and steamer
                    inMemorySegment.addPostList(token, docId, pos++);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void search(String... text) {
        System.out.println(appContext.getDocumentStore().toString());

        Map<String, List<String>> dictionary = appContext.getInMemoryDictionary();
        Map<Integer, DiscSegment> discSegments = appContext.getDiscSegments();
        InMemorySegment inMemorySegment = appContext.getInMemorySegment();
        for (String token: text) {
            System.out.println("*** " + token + " ***");
            System.out.println("in memory:\n" + inMemorySegment.getPostList(token).toString());
            List<String> res = dictionary.get(token);
            if (res == null || res.isEmpty()) {
                System.out.println(token + " not found...");
            } else {
                for (String p : res) {
                    String[] elem = p.split(" ");
                    if (elem.length > 0) {
                        System.out.println("on disc:\n" + discSegments.get(Integer.valueOf(elem[0])).getPostList(Integer.valueOf(elem[1])).toString());
                    }
                }
            }
        }
    }
}
