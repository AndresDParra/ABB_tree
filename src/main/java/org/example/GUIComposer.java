package org.example;

import javax.swing.*;

public class GUIComposer extends JFrame {
    private TreeComposer treeComposer;
    private TreePanel treePanel;
    private ControlPanel controlPanel;
    private ABBTree tree;

    public GUIComposer() {
        setTitle("ABB Tree Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setResizable(true);

        tree = new ABBTree();

        initializeComponents();
        setupListeners();
    }

    private void initializeComponents() {
        treePanel = new TreePanel(tree);
        controlPanel = new ControlPanel();

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, controlPanel);
        splitPane.setDividerLocation(500);

        setContentPane(splitPane);
    }

    private void setupListeners() {
        controlPanel.setInsertListener(e -> handleInsert());
        controlPanel.setDeleteListener(e -> handleDelete());
        controlPanel.setSearchListener(e -> handleSearch());
        controlPanel.setInorderListener(e -> handleInorder());
        controlPanel.setPreorderListener(e -> handlePreorder());
        controlPanel.setPostorderListener(e -> handlePostorder());
        controlPanel.setHeightListener(e -> handleHeight());
        controlPanel.setClearListener(e -> controlPanel.clearOutput());
    }

    private void handleInsert() {
        try {
            int value = Integer.parseInt(controlPanel.getInputValue());
            tree.insert(value);
            controlPanel.appendOutput("✓ Inserted: " + value);
            controlPanel.clearInput();
            treePanel.repaint();
        } catch (NumberFormatException ex) {
            controlPanel.appendOutput("✗ Invalid input!");
        }
    }

    private void handleDelete() {
        try {
            int value = Integer.parseInt(controlPanel.getInputValue());
            tree.delete(value);
            controlPanel.appendOutput("✓ Deleted: " + value);
            controlPanel.clearInput();
            treePanel.repaint();
        } catch (NumberFormatException ex) {
            controlPanel.appendOutput("✗ Invalid input!");
        }
    }

    private void handleSearch() {
        try {
            int value = Integer.parseInt(controlPanel.getInputValue());
            boolean found = tree.search(value);
            controlPanel.appendOutput("Value " + value + " found: " + found);
            controlPanel.clearInput();
        } catch (NumberFormatException ex) {
            controlPanel.appendOutput("✗ Invalid input!");
        }
    }

    private void handleInorder() {
        controlPanel.appendOutput("Inorder: " + tree.inorderString());
    }

    private void handlePreorder() {
        controlPanel.appendOutput("Preorder: " + tree.preorderString());
    }

    private void handlePostorder() {
        controlPanel.appendOutput("Postorder: " + tree.postorderString());
    }

    private void handleHeight() {
        controlPanel.appendOutput("Tree height: " + tree.getHeight());
    }
}

