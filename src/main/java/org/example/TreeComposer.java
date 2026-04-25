package org.example;

public class TreeComposer {
    private ABBTree tree;

    public TreeComposer() {
        this.tree = new ABBTree();
    }

    public void addValue(int value) {
        tree.insert(value);
        System.out.println("Inserted: " + value);
    }

    public void removeValue(int value) {
        tree.delete(value);
        System.out.println("Deleted: " + value);
    }

    public void findValue(int value) {
        boolean found = tree.search(value);
        System.out.println("Value " + value + " found: " + found);
    }

    public void displayInorder() {
        System.out.print("Inorder traversal: ");
        tree.inorder();
    }

    public void displayPreorder() {
        System.out.print("Preorder traversal: ");
        tree.preorder();
    }

    public void displayPostorder() {
        System.out.print("Postorder traversal: ");
        tree.postorder();
    }

    public void showHeight() {
        System.out.println("Tree height: " + tree.getHeight());
    }

    public void showStatus() {
        System.out.println("Tree is empty: " + tree.isEmpty());
    }
}

