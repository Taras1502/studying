package algorithms.dataStructures.rbt;

/**
 * Created by Taras.Mykulyn on 17.11.2015.
 */
public class RedBlackTree<T extends Comparable> {
    private Node root;

    public void insert(T elem) {
        if (root == null) {
            root = new Node(elem, false);
        } else {
            root = insert(root, elem);
        }
    }

    private Node insert(Node node, T elem) {
        if (node == null) {
            return new Node(elem, true);
        } else if (node.value.compareTo(elem) > 0) {
            node.right = insert(node.right, elem);
        } else if (node.value.compareTo(elem) < 0) {
            node.left = insert(node.left, elem);
        }
        return node;
    }

    class Node {
        T value;
        Node left;
        Node right;
        boolean isRed;

        public Node(T value, boolean isRed) {
            this.value = value;
            this.isRed = isRed;
            left = null;
            right = null;
        }
    }
}
