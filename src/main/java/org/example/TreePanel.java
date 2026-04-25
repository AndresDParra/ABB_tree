package org.example;

import javax.swing.*;
import java.awt.*;

public class TreePanel extends JPanel {
    private final ABBTree tree;

    public TreePanel(ABBTree tree) {
        this.tree = tree;
        this.setBackground(Color.WHITE);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Node root = tree.getRoot();
        if (root != null) {
            drawTree(g2, root, getWidth() / 2, 40, Math.max(40, getWidth() / 4));
        }
    }

    private void drawTree(Graphics2D g, Node node, int x, int y, int offset) {
        if (node == null) return;

        int nextOffset = Math.max(20, offset / 2);

        if (node.left != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x - offset, y + 60);
            drawTree(g, node.left, x - offset, y + 60, nextOffset);
        }

        if (node.right != null) {
            g.setColor(Color.BLACK);
            g.drawLine(x, y, x + offset, y + 60);
            drawTree(g, node.right, x + offset, y + 60, nextOffset);
        }

        g.setColor(Color.BLUE);
        g.fillOval(x - 20, y - 20, 40, 40);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        FontMetrics fm = g.getFontMetrics();
        String text = String.valueOf(node.value);
        int textX = x - fm.stringWidth(text) / 2;
        int textY = y + (fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(text, textX, textY);
    }
}
