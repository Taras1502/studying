package searchEngine.core;

import searchEngine.interfaces.SearchResult;
import searchEngine.interfaces.SegmentManager;
import searchEngine.newStructure.Dictionary;
import searchEngine.newStructure.DocumentStore;

import java.util.Map;
import java.util.Scanner;

/**
 * Created by macbookpro on 4/27/16.
 */
public class SearchManager extends Thread {
    private Dictionary dictionary;
    private SegmentManager segmentManager;
    private DocumentStore documentStore;

    @Override
    public void run() {
        Scanner in = new Scanner(System.in);
        do {
            String text = in.nextLine();
            System.out.println(search(text.split(" ")));
        } while(in.hasNextLine());
    }

    private SearchResult search(String... tokens) {
        for (String token: tokens) {

            Map<Integer, Integer> res = dictionary.getTokenData(token);

        }
        return null;
    }
}
