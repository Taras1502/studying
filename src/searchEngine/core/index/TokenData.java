package searchEngine.core.index;

import searchEngine.core.PostList;
import searchEngine.core.segments.discSegment.DiscSegment;

/**
 * Created by Taras.Mykulyn on 06.05.2016.
 */
public class TokenData implements Comparable<TokenData> {
    private String token;
    private PostList postList;



    @Override
    public int compareTo(TokenData o) {
        return token.compareTo(o.token);
    }
}
