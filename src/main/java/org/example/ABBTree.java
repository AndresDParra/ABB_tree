package org.example;

public class ABBTree {
    private Node root;

    public ABBTree() {
        this.root = null;
    }

    public void insert(int value) {
        root = insertRecursive(root, value);
    }

    private Node insertRecursive(Node node, int value) {
        if (node == null) {
            return new Node(value);
        }
        if (value < node.value) {
            node.left = insertRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = insertRecursive(node.right, value);
        }
        return node;
    }

    public boolean search(int value) {
        return searchRecursive(root, value);
    }

    private boolean searchRecursive(Node node, int value) {
        if (node == null) {
            return false;
        }
        if (value == node.value) {
            return true;
        } else if (value < node.value) {
            return searchRecursive(node.left, value);
        } else {
            return searchRecursive(node.right, value);
        }
    }

    public void delete(int value) {
        root = deleteRecursive(root, value);
    }

    private Node deleteRecursive(Node node, int value) {
        if (node == null) {
            return null;
        }
        if (value < node.value) {
            node.left = deleteRecursive(node.left, value);
        } else if (value > node.value) {
            node.right = deleteRecursive(node.right, value);
        } else {
            if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            Node temp = findMin(node.right);
            node.value = temp.value;
            node.right = deleteRecursive(node.right, temp.value);
        }
        return node;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    public void inorder() {
        inorderRecursive(root);
        System.out.println();
    }

    private void inorderRecursive(Node node) {
        if (node != null) {
            inorderRecursive(node.left);
            System.out.print(node.value + " ");
            inorderRecursive(node.right);
        }
    }

    public void preorder() {
        preorderRecursive(root);
        System.out.println();
    }

    private void preorderRecursive(Node node) {
        if (node != null) {
            System.out.print(node.value + " ");
            preorderRecursive(node.left);
            preorderRecursive(node.right);
        }
    }

    public void postorder() {
        postorderRecursive(root);
        System.out.println();
    }

    private void postorderRecursive(Node node) {
        if (node != null) {
            postorderRecursive(node.left);
            postorderRecursive(node.right);
            System.out.print(node.value + " ");
        }
    }

    public int getHeight() {
        return getHeightRecursive(root);
    }

    private int getHeightRecursive(Node node) {
        if (node == null) {
            return -1;
        }
        return 1 + Math.max(getHeightRecursive(node.left), getHeightRecursive(node.right));
    }

    public boolean isEmpty() {
        return root == null;
    }

    public Node getRoot() {
        return root;
    }

    public String inorderString() {
        StringBuilder sb = new StringBuilder();
        inorderToString(root, sb);
        return sb.toString().trim();
    }

    private void inorderToString(Node node, StringBuilder sb) {
        if (node == null) return;
        inorderToString(node.left, sb);
        sb.append(node.value).append(' ');
        inorderToString(node.right, sb);
    }

    public String preorderString() {
        StringBuilder sb = new StringBuilder();
        preorderToString(root, sb);
        return sb.toString().trim();
    }

    private void preorderToString(Node node, StringBuilder sb) {
        if (node == null) return;
        sb.append(node.value).append(' ');
        preorderToString(node.left, sb);
        preorderToString(node.right, sb);
    }

    public String postorderString() {
        StringBuilder sb = new StringBuilder();
        postorderToString(root, sb);
        return sb.toString().trim();
    }

    private void postorderToString(Node node, StringBuilder sb) {
        if (node == null) return;
        postorderToString(node.left, sb);
        postorderToString(node.right, sb);
        sb.append(node.value).append(' ');
    }
}
