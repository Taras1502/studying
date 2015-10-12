package algorithms.unionFindProblem;

/**
 * Created by Taras.Mykulyn on 09.10.2015.
 */
public class QuickUnionFind implements UnionFind {
    private int numberOfNodes;
    private int[] array;

    public QuickUnionFind(int numberOfNodes) {
        array = new int[this.numberOfNodes = numberOfNodes];
        for (int i = 0; i < numberOfNodes; i++) {
            array[i] = i;
        }
    }

    @Override
    public void union(int p, int q) {
        if (p > q) {
            int temp = p;
            p = q;
            q = temp;
        }
        if (array[p] != array[q]) {
            for (int i = 0; i < numberOfNodes; i++) {
                if (array[i] == array[q]) {
                    array[i] = array[p];
                }
            }
        }
    }

    public void disconnect(int p, int q) {
        if (p > q) {
            array[p] = p;
        } else {
            array[q] = q;
        }
    }

    @Override
    public int find(int p) {
        return array[p];
    }

    @Override
    public boolean connected(int p, int q) {
        return array[p] == array[q];
    }

    @Override
    public int count() {
        return 0;
    }

    public static void main(String[] args) {
        UnionFind uf = new QuickUnionFind(10);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(2, 5);

        System.out.println(uf.connected(3, 4));

        uf.disconnect(3, 4);
        System.out.println(uf.connected(2, 5));

    }
}
