package searchEngine.newStructure;

/**
 * Created by macbookpro on 4/20/16.
 */
public class SearchEngine {

    public static void main(String[] args) {
        IndexManager i = IndexManager.create("/Users/macbookpro/Desktop/workingDir");
        long start = System.currentTimeMillis();
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big.txt");
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big_copy.txt");
        i.addFileToIndex("/Users/macbookpro/Desktop/test/english.100mb");
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big_copy_2.txt");
        long p1 = System.currentTimeMillis();
        System.out.println(p1 - start);
        i.search("good");
    }
}
