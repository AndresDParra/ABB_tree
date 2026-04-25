package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ControlPanel extends JPanel {
    private JTextField inputField;
    private JButton insertBtn, deleteBtn, searchBtn;
    private JButton inorderBtn, preorderBtn, postorderBtn;
    private JButton heightBtn, clearBtn;
    private JTextArea outputArea;

    public ControlPanel() {
        setLayout(new BorderLayout(10, 10));
        setPadding(10);

        // Input Panel
        JPanel inputPanel = createInputPanel();
        add(inputPanel, BorderLayout.NORTH);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.CENTER);

        // Output Panel
        JPanel outputPanel = createOutputPanel();
        add(outputPanel, BorderLayout.SOUTH);
    }

    private JPanel createInputPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Input"));
        
        panel.add(new JLabel("Value:"));
        inputField = new JTextField(10);
        panel.add(inputField);
        
        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 3, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Operations"));

        insertBtn = new JButton("Insert");
        deleteBtn = new JButton("Delete");
        searchBtn = new JButton("Search");
        inorderBtn = new JButton("Inorder");
        preorderBtn = new JButton("Preorder");
        postorderBtn = new JButton("Postorder");
        heightBtn = new JButton("Height");
        clearBtn = new JButton("Clear Output");

        panel.add(insertBtn);
        panel.add(deleteBtn);
        panel.add(searchBtn);
        panel.add(inorderBtn);
        panel.add(preorderBtn);
        panel.add(postorderBtn);
        panel.add(heightBtn);
        panel.add(clearBtn);

        return panel;
    }

    private JPanel createOutputPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Output"));
        
        outputArea = new JTextArea(5, 30);
        outputArea.setEditable(false);
        outputArea.setFont(new Font("Courier", Font.PLAIN, 12));
        JScrollPane scrollPane = new JScrollPane(outputArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void setPadding(int padding) {
        setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
    }

    public String getInputValue() {
        return inputField.getText();
    }

    public void clearInput() {
        inputField.setText("");
    }

    public void appendOutput(String text) {
        outputArea.append(text + "\n");
    }

    public void clearOutput() {
        outputArea.setText("");
    }

    public void setInsertListener(ActionListener listener) {
        insertBtn.addActionListener(listener);
    }

    public void setDeleteListener(ActionListener listener) {
        deleteBtn.addActionListener(listener);
    }

    public void setSearchListener(ActionListener listener) {
        searchBtn.addActionListener(listener);
    }

    public void setInorderListener(ActionListener listener) {
        inorderBtn.addActionListener(listener);
    }

    public void setPreorderListener(ActionListener listener) {
        preorderBtn.addActionListener(listener);
    }

    public void setPostorderListener(ActionListener listener) {
        postorderBtn.addActionListener(listener);
    }

    public void setHeightListener(ActionListener listener) {
        heightBtn.addActionListener(listener);
    }

    public void setClearListener(ActionListener listener) {
        clearBtn.addActionListener(listener);
    }
}

