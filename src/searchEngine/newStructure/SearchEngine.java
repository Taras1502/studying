package searchEngine.newStructure;

/**
 * Created by macbookpro on 4/20/16.
 */
public class SearchEngine {

    public static void main(String[] args) {
        IndexManager i = IndexManager.create("D:\\workingDir");
        i.addFileToIndex("D:\\docs\\courierv34man.txt");
        i.addFileToIndex("D:\\docs\\jargn10.txt");
//        i.addFileToIndex("D:\\docs");
        i.search("good");
    }
}
