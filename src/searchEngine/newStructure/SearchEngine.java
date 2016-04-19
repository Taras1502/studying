package searchEngine.newStructure;

/**
 * Created by macbookpro on 4/20/16.
 */
public class SearchEngine {

    public static void main(String[] args) {
        IndexManager i = new IndexManager(ApplicationContext.create("/Users/macbookpro/Desktop/workingDir"));
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big.txt");
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big_copy.txt");
        i.addFileToIndex("/Users/macbookpro/Desktop/test/big_copy_2.txt");
        i.search("good");
    }
}
