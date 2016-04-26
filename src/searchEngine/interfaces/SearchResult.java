package searchEngine.interfaces;

import java.util.List;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface SearchResult {
    int getDocNumber();

    List<String> getDocPaths();

    String getDocSnippet(int docId);
}
