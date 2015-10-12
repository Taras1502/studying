package algorithms.unionFindProblem;

/**
 * Created by Taras.Mykulyn on 12.10.2015.
 */
public class QuickInsertUF implements UnionFind {
    private int[] array;
    private int[] treeSize;
    private int numberOfComponents;

    public QuickInsertUF(int numberOfNodes) {
        array = new int[numberOfNodes];
        treeSize = new int[numberOfNodes];
        this.numberOfComponents = numberOfNodes;
        for (int i = 0; i < numberOfNodes; i++) {
            array[i] = i;
            treeSize[i] = 1;
        }
    }

    @Override
    public void union(int p, int q) {
        int pRoot = findRoot(p);
        int qRoot = findRoot(q);
        if (pRoot != qRoot) {
            numberOfComponents--;
            if (treeSize[p] < treeSize[q]) {
                array[pRoot] = qRoot;
                treeSize[qRoot] += treeSize[pRoot];
            } else {
                array[qRoot] = pRoot;
                treeSize[pRoot] += treeSize[qRoot];
            }
        }
    }

    @Override
    public void disconnect(int p, int q) {
        int pRoot = findRoot(p);
        int qRoot = findRoot(q);
        if (pRoot == qRoot) {
            numberOfComponents++;
            if (p != pRoot) {
                array[p] = p;
                treeSize[qRoot] -= treeSize[p];
            } else {
                array[q] = q;
                treeSize[qRoot] -= treeSize[q];
            }
        }
    }

    @Override
    public int find(int p) {
        return findRoot(p);
    }

    @Override
    public boolean connected(int p, int q) {
        return findRoot(p) == findRoot(q);
    }

    @Override
    public int count() {
        return numberOfComponents;
    }

    private int findRoot(int index) {
        int root = array[index];
        while(root != array[root]) {
            root = array[root];
        }
        return root;
    }

    public void printTreeSize() {
        System.out.println();
        for (int i:  treeSize) {
            System.out.println(i);
        }
        System.out.println();
    }

    public static void main(String[] args) {
        UnionFind uf = new QuickInsertUF(10);
        uf.union(2, 3);
        uf.union(4, 5);
        uf.union(2, 5);

        ((QuickInsertUF) uf).printTreeSize();
        System.out.println(uf.connected(3, 4) + "   " + uf.count());

        uf.disconnect(3, 4);
        System.out.println(uf.connected(2, 5) + "   " + uf.count());
        ((QuickInsertUF) uf).printTreeSize();
    }
}
