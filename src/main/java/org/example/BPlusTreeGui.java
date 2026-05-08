package org.example;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

public class BPlusTreeGui extends JFrame {
    private final JTextField orderField = new JTextField("4", 6);
    private final JTextField keyField = new JTextField(10);
    private final JTextField valueField = new JTextField(12);
    private final JTextField searchField = new JTextField(10);
    private final JTextArea outputArea = new JTextArea(10, 50);
    private final TreePanel treePanel = new TreePanel();

    private BPlusTree<Integer, String> tree = new BPlusTree<>(4);

    public BPlusTreeGui() {
        super("B+ Tree GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(8, 8));

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton createButton = new JButton("Create Tree");
        JButton insertButton = new JButton("Insert");
        JButton searchButton = new JButton("Search");

        createButton.addActionListener(e -> createTree());
        insertButton.addActionListener(e -> insertKey());
        searchButton.addActionListener(e -> searchKey());

        controls.add(new JLabel("Order:"));
        controls.add(orderField);
        controls.add(createButton);

        controls.add(new JLabel("Key:"));
        controls.add(keyField);
        controls.add(new JLabel("Value:"));
        controls.add(valueField);
        controls.add(insertButton);

        controls.add(new JLabel("Search Key:"));
        controls.add(searchField);
        controls.add(searchButton);

        outputArea.setEditable(false);
        outputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                new JScrollPane(treePanel),
                new JScrollPane(outputArea)
        );
        splitPane.setResizeWeight(0.8);

        add(controls, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);

        refreshOutput();
        pack();
        setSize(1200, 900);
        setLocationRelativeTo(null);
    }

    private void createTree() {
        try {
            int order = Integer.parseInt(orderField.getText().trim());
            tree = new BPlusTree<>(order);
            refreshOutput();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Order must be an integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void insertKey() {
        try {
            int key = Integer.parseInt(keyField.getText().trim());
            String value = valueField.getText();
            tree.insert(key, value);
            refreshOutput();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Key must be an integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchKey() {
        try {
            int key = Integer.parseInt(searchField.getText().trim());
            String result = tree.search(key);
            JOptionPane.showMessageDialog(
                    this,
                    result == null ? "Key not found." : "Value: " + result,
                    "Search result",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Search key must be an integer.", "Invalid input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshOutput() {
        treePanel.setSnapshot(tree.snapshot());
        outputArea.setText(
                "Tree structure:\n" + tree.describeTree() +
                "\n\nLeaf order:\n" + tree.describeLeaves()
        );
    }

    private final class TreePanel extends JPanel {
        private static final int MARGIN = 30;
        private static final int LEVEL_GAP = 90;
        private static final int SIBLING_GAP = 30;
        private static final int PADDING_X = 14;
        private static final int PADDING_Y = 10;

        private final Font nodeFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        private BPlusTree.TreeSnapshot snapshot;

        private TreePanel() {
            setBackground(Color.WHITE);
        }

        void setSnapshot(BPlusTree.TreeSnapshot snapshot) {
            this.snapshot = snapshot;
            updatePreferredSize();
            revalidate();
            repaint();
        }

        private void updatePreferredSize() {
            if (snapshot == null || snapshot.getRoot() == null) {
                setPreferredSize(new Dimension(900, 600));
                return;
            }

            FontMetrics fm = getFontMetrics(nodeFont);
            Layout layout = buildLayout(fm);
            int width = (int) Math.ceil(layout.maxX + MARGIN);
            int height = (int) Math.ceil(layout.maxY + MARGIN);
            setPreferredSize(new Dimension(Math.max(width, 900), Math.max(height, 600)));
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (snapshot == null || snapshot.getRoot() == null) {
                g.drawString("Insert values to build the B+ tree.", 20, 30);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            try {
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setFont(nodeFont);

                FontMetrics fm = g2.getFontMetrics();
                Layout layout = buildLayout(fm);

                g2.setColor(new Color(90, 90, 90));
                g2.setStroke(new BasicStroke(1.4f));

                for (Line2D line : layout.lines) {
                    g2.draw(line);
                }

                for (PositionedNode positioned : layout.nodes) {
                    drawNode(g2, fm, positioned);
                }
            } finally {
                g2.dispose();
            }
        }

        private Layout buildLayout(FontMetrics fm) {
            Layout layout = new Layout();
            layoutNode(snapshot.getRoot(), 0, layout, fm);
            return layout;
        }

        private PositionedNode layoutNode(
                BPlusTree.NodeView node,
                int depth,
                Layout layout,
                FontMetrics fm
        ) {
            List<String> lines = node.getDisplayLines();
            int width = 0;
            for (String line : lines) {
                width = Math.max(width, fm.stringWidth(line));
            }
            width += PADDING_X * 2;
            int height = lines.size() * fm.getHeight() + PADDING_Y * 2;

            if (node.isLeaf()) {
                double x = layout.nextX;
                double y = MARGIN + depth * LEVEL_GAP;

                PositionedNode positioned = new PositionedNode(node, x, y, width, height, lines);
                layout.nodes.add(positioned);

                layout.nextX += width + SIBLING_GAP;
                layout.maxX = Math.max(layout.maxX, x + width);
                layout.maxY = Math.max(layout.maxY, y + height);
                return positioned;
            }

            List<PositionedNode> children = new ArrayList<>();
            for (BPlusTree.NodeView child : node.getChildren()) {
                children.add(layoutNode(child, depth + 1, layout, fm));
            }

            double firstCenter = children.get(0).centerX();
            double lastCenter = children.get(children.size() - 1).centerX();
            double center = (firstCenter + lastCenter) / 2.0;

            double x = center - width / 2.0;
            double y = MARGIN + depth * LEVEL_GAP;

            PositionedNode positioned = new PositionedNode(node, x, y, width, height, lines);
            layout.nodes.add(positioned);

            for (PositionedNode child : children) {
                layout.lines.add(new Line2D.Double(
                        positioned.centerX(),
                        positioned.bottom(),
                        child.centerX(),
                        child.top()
                ));
            }

            layout.maxX = Math.max(layout.maxX, x + width);
            layout.maxY = Math.max(layout.maxY, y + height);
            return positioned;
        }

        private void drawNode(Graphics2D g2, FontMetrics fm, PositionedNode node) {
            int x = (int) Math.round(node.x);
            int y = (int) Math.round(node.y);
            int w = node.width;
            int h = node.height;

            g2.setColor(node.view.isLeaf() ? new Color(232, 245, 233) : new Color(227, 242, 253));
            g2.fillRoundRect(x, y, w, h, 16, 16);

            g2.setColor(new Color(60, 60, 60));
            g2.drawRoundRect(x, y, w, h, 16, 16);

            g2.setColor(new Color(30, 30, 30));
            int textY = y + PADDING_Y + fm.getAscent();
            for (String line : node.lines) {
                int textX = x + (w - fm.stringWidth(line)) / 2;
                g2.drawString(line, textX, textY);
                textY += fm.getHeight();
            }
        }

        private final class Layout {
            private final List<PositionedNode> nodes = new ArrayList<>();
            private final List<Line2D> lines = new ArrayList<>();
            private double nextX = MARGIN;
            private double maxX = 0;
            private double maxY = 0;
        }

        private final class PositionedNode {
            private final BPlusTree.NodeView view;
            private final double x;
            private final double y;
            private final int width;
            private final int height;
            private final List<String> lines;

            private PositionedNode(BPlusTree.NodeView view, double x, double y, int width, int height, List<String> lines) {
                this.view = view;
                this.x = x;
                this.y = y;
                this.width = width;
                this.height = height;
                this.lines = lines;
            }

            private double centerX() {
                return x + width / 2.0;
            }

            private double top() {
                return y;
            }

            private double bottom() {
                return y + height;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new BPlusTreeGui().setVisible(true));
    }
}
