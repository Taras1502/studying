package searchEngine.interfaces;

import java.util.Map;

/**
 * Created by Taras.Mykulyn on 26.04.2016.
 */
public interface Dictionary {
    void addToken(String token, int segmentId, int pos);

    Map<Integer, Integer> getTokenData(String token);

    /*
    This operation writes in-memory dictionary to disc by serialization of the dictionary object.
    Originally designed to be called after creating or merging disc segments.
    */
    boolean commit();
}
