package algorithms.unionFindProblem;

/**
 * Created by Taras.Mykulyn on 09.10.2015.
 */
public interface UnionFind {
    // creates a connection between node q and p if they are not connected
    void union(int p, int q);

    // removes connection between node q and p if there is one
    void disconnect(int p, int q);

    // returns the component of p
    int find(int p);

    // checks if two nodes are connected
    boolean connected(int p, int q);

    // returns the number of components
    int count();

}
