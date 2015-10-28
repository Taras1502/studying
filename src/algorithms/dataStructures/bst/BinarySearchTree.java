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
        System.out.println("Adding...");
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

    private Node getParent(Node node, T elem) {
        if (node != null) {
            if (elem.compareTo(node.value) == 0) {
                return null;
            } else if (elem.compareTo(node.value) < 0) {
                return getParent(node.left, node, elem);
            } else {
                return getParent(node.right, node, elem);
            }
        } else {
            return null;
        }
    }

    private Node getParent(Node current, Node parent, T elem) {
        if (current != null) {
            if (elem.compareTo(current.value) == 0) {
                return parent;
            } else if (elem.compareTo(current.value) < 0) {
                return getParent(current.left, current, elem);
            } else {
                return getParent(current.right, current, elem);
            }
        } else {
            return null;
        }
    }

    private Node removeMinNode(Node node) {
        if (node.left != null) {
            node.left = removeMinNode(node.left);
        } else if (node.right != null) {
            Node minNode = node.right;
            node.left = node.right;
            return minNode;
        } else {
            return null;
        }
        return node;
    }

    public void remove(T elem) {
        if (root == null) return;
        Node parent = getParent(root, elem);
        if (parent != null) {
            if (parent.value.compareTo(elem) < 0) {
                parent.right = resetNode(parent.right);
            } else {
                parent.left = resetNode(parent.left);
            }
        } else if (root.value.compareTo(elem) == 0) {
            root = resetNode(root);
        }
    }

    private Node resetNode(Node node) {
        if (node.left == null && node.right == null) {
            return null;
        } else if (node.left != null && node.right == null) {
            return node.left;
        } else if (node.left == null && node.right != null) {
            return node.right;
        } else {
            return removeMinNode(node.right);
        }
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

    private void print() {
    }

    public static void main(String[] args) {
        BinarySearchTree<Integer> bst = new BinarySearchTree<>();
        bst.add(2);
        bst.add(5);
        bst.add(5);
        bst.add(4);

        System.out.println(bst.contains(2));
        System.out.println(bst.contains(5));
        System.out.println(bst.contains(4));
        System.out.println();

        bst.remove(5);
        System.out.println(bst.contains(2));
        System.out.println(bst.contains(5));
        System.out.println(bst.contains(4));
        System.out.println();

        bst.remove(4);
        System.out.println(bst.contains(2));
        System.out.println(bst.contains(5));
        System.out.println(bst.contains(4));
        System.out.println();

        bst.remove(2);
        System.out.println(bst.contains(2));
        System.out.println(bst.contains(5));
        System.out.println(bst.contains(4));

        bst.remove(2);
    }
}
