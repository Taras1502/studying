package searchEngine.index;

import searchEngine.interfaces.SearchResult;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface IndexManager {
    void indexFile(String path);

    void indexFiles(String dirPath, boolean recursively);

    SearchResult search(String... tokens);

    void close();
}
