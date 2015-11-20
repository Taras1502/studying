package algorithms.dataStructures.bst;

/**
 * Created by Taras.Mykulyn on 27.10.2015.
 */
public class BinarySearchTree<T extends Comparable> {
    private Node root;
    private int size;

    public BinarySearchTree() {
        size = 0;
    }

    public void add(T elem) {
        root = add(root, elem);
        System.out.println("Adding element: " + elem);
    }

    private Node add(Node node, T elem) {
        if (node != null) {
            if (elem.compareTo(node.value) < 0) {
                node.left = add(node.left, elem);
            } else if (elem.compareTo(node.value) > 0) {
                node.right = add(node.right, elem);
            }
        } else {
            size++;
            return new Node(elem);
        }
        return node;
    }

    private Node getNode(Node node, T elem) {
        if (node != null) {
            if (elem.compareTo(node.value) == 0) {
                return node;
            } else if (elem.compareTo(node.value) < 0) {
                return getNode(node.left, elem);
            } else {
                return getNode(node.right, elem);
            }
        } else {
            return null;
        }
    }

    public void removeMinNode() {
        if (root != null) {
            Node minNode = new Node(root.value);
            root = removeMinNode(root, minNode);
            size--;
        }
    }

    private Node removeMinNode(Node node, Node minNode) {
        if (node.left != null) {
            Node leftNode = node;
            minNode.value = node.left.value;
            node.left = removeMinNode(node.left, minNode);
            return leftNode;
        } else {
            return node.right;
        }
    }

    public void removeMaxNode() {
        if (root != null) {
            root = removeMaxNode(root);
            size--;
        }
    }

    private Node removeMaxNode(Node node) {
        if (node.right != null) {
            node.right = removeMaxNode(node.right);
            return node;
        } else {
            return node.left;
        }
    }

    public void delete(T element) {
        root = delete(root, element);
    }

    private Node delete(Node node, T elem) {
        if (node.value.compareTo(elem) > 0) {
            node.left = delete(node.left, elem);
        } else if (node.value.compareTo(elem) < 0) {
            node.right = delete(node.right, elem);
        } else {
            size--;
            if (node.left != null && node.right != null) {
                Node minNode = new Node(node.right.value);
                node.right = removeMinNode(node.right, minNode);
                node.value = minNode.value;
            } else if (node.left != null) {
                return node.left;
            } else if (node.right != null) {
                return node.right;
            } else {
                return null;
            }
        }
        return node;
    }

    public boolean contains(T elem) {
        return getNode(root, elem) != null;
    }

    private Node minNode(Node node) {
        if (node.left != null) {
            return minNode(node.left);
        } else {
            return node;
        }
    }

    private Node maxNode(Node node) {
        if (node.right != null) {
            return maxNode(node.right);
        } else {
            return node;
        }
    }

    public void inOrder() {
        System.out.println("Size: " + size);
        inOrder(root);
    }

    private void inOrder(Node node) {
        if (node == null) {
            System.out.println("No items");
            return;
        }
        if (node.left != null) {
            inOrder(node.left);
        }
        System.out.print(node.value + " ");
        if (node.right != null) {
            inOrder(node.right);
        }
    }

    class Node {
        T value;
        Node left;
        Node right;

        public Node(T value) {
            this.value = value;
            left = null;
            right = null;
        }
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        for (int i = 0; i < 10000; i++) {
            bst.add(i);
        }
        System.out.println(bst.contains(555));
        bst.delete(555);
        System.out.println(bst.contains(555));
//        bst.inOrder();


//        bst.delete(2);
//        System.out.println(bst.contains(2));
//        System.out.println(bst.contains(5));
//        System.out.println(bst.contains(4));
//        System.out.println(bst.contains(6));
//        System.out.println();
//
//        bst.delete(5);
//        System.out.println(bst.contains(2));
//        System.out.println(bst.contains(5));
//        System.out.println(bst.contains(4));
//        System.out.println(bst.contains(6));
//        System.out.println();
//
//        bst.delete(4);
//        System.out.println(bst.contains(2));
//        System.out.println(bst.contains(5));
//        System.out.println(bst.contains(4));
//        System.out.println(bst.contains(6));
//        System.out.println();
//
//        bst.delete(6);
//        System.out.println(bst.contains(2));
//        System.out.println(bst.contains(5));
//        System.out.println(bst.contains(4));
//        System.out.println(bst.contains(6));
//        System.out.println();
    }
}
