package org.example;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

/** Panel visual que dibuja el árbol binario de búsqueda. Se redibuja cada vez que el árbol cambia. */
public class TreePanel extends JPanel {
    private static final Color NODE_FILL      = new Color(242, 84, 91);   // color de nodo normal
    private static final Color NODE_FILL_2C   = new Color(200, 60, 80);   // color de nodo con dos hijos
    private static final Color NODE_BORDER    = new Color(243, 247, 240);
    private static final Color NODE_BORDER_2C = new Color(255, 160, 120);
    private static final Color TEXT_COLOR     = new Color(243, 247, 240);
    private static final Color EDGE_COLOR     = new Color(140, 94, 88);
    private static final Color BG_COLOR       = new Color(20, 40, 50);
    private static final Color EMPTY_COLOR    = new Color(100, 130, 140);

    private ABBTree tree;

    private static final int NODE_R         = 26; // radio de cada nodo en píxeles
    private static final int VERTICAL_GAP   = 75; // espacio vertical entre niveles
    private static final int BASE_HORIZ_GAP = 30; // espacio base entre nodos (no usado directamente)

    /** Crea el panel asociado al árbol dado. */
    public TreePanel(ABBTree tree) {
        this.tree = tree;
        setBackground(BG_COLOR);
        setPreferredSize(new Dimension(800, 700));
    }

    /** Dibuja el árbol completo cada vez que Swing solicita repintar el panel. Aplica escala automática para que siempre quepa. */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (tree.isEmpty()) {
            drawEmptyMessage(g2);
            return;
        }

        // 1. Asignar coordenadas X relativas a cada nodo (recorrido inorden)
        int[] xPos = {0};
        assignX(tree.getRoot(), xPos);

        // 2. Medir el ancho y alto total que ocupa el árbol
        int[] minX = {Integer.MAX_VALUE}, maxX = {Integer.MIN_VALUE};
        collectXRange(tree.getRoot(), minX, maxX);
        int treeW = maxX[0] - minX[0] + NODE_R * 2;
        int treeH = (tree.getHeight() + 1) * VERTICAL_GAP + NODE_R * 4;

        int panelW = Math.max(getWidth(),  1);
        int panelH = Math.max(getHeight(), 1);

        // 3. Calcular escala para que el árbol siempre quepa en el panel (máximo 1.0)
        double scale = Math.min(1.0, Math.min(
            (double) panelW / treeW,
            (double) panelH / treeH
        ) * 0.92);

        // 4. Centrar el árbol en el panel aplicando translate + scale
        double scaledW = treeW * scale;
        double scaledH = treeH * scale;
        double tx = (panelW - scaledW) / 2.0 - minX[0] * scale + NODE_R * scale;
        double ty = (panelH - scaledH) / 2.0 + NODE_R * scale;

        g2.translate(tx, ty);
        g2.scale(scale, scale);

        int drawY = NODE_R + 10;

        // 5. Dibujar aristas primero, luego nodos encima
        drawEdges(g2, tree.getRoot(), drawY, 0);
        drawNodes(g2, tree.getRoot(), drawY, 0);
    }

    /** Asigna la coordenada X a cada nodo usando recorrido inorden, garantizando que no se superpongan. */
    private void assignX(Node node, int[] counter) {
        if (node == null) return;
        assignX(node.left, counter);
        node.x = counter[0];
        counter[0] += NODE_R * 2 + 10; // paso = diámetro del nodo + margen mínimo
        assignX(node.right, counter);
    }

    /** Recorre todos los nodos y actualiza el mínimo y máximo valor de X encontrados. */
    private void collectXRange(Node node, int[] minX, int[] maxX) {
        if (node == null) return;
        if (node.x < minX[0]) minX[0] = node.x;
        if (node.x > maxX[0]) maxX[0] = node.x;
        collectXRange(node.left,  minX, maxX);
        collectXRange(node.right, minX, maxX);
    }

    /** Desplaza la coordenada X de todos los nodos sumando un offset dado. */
    private void shiftX(Node node, int offset) {
        if (node == null) return;
        node.x += offset;
        shiftX(node.left,  offset);
        shiftX(node.right, offset);
    }

    /** Dibuja recursivamente las líneas que conectan cada nodo con sus hijos. */
    private void drawEdges(Graphics2D g, Node node, int y, int level) {
        if (node == null) return;
        int childY = y + VERTICAL_GAP;

        if (node.left != null) {
            drawEdge(g, node.x, y, node.left.x, childY);
            drawEdges(g, node.left, childY, level + 1);
        }
        if (node.right != null) {
            drawEdge(g, node.x, y, node.right.x, childY);
            drawEdges(g, node.right, childY, level + 1);
        }
    }

    /** Dibuja una línea entre dos nodos, recortada en los bordes del círculo para no tapar los nodos. */
    private void drawEdge(Graphics2D g, int x1, int y1, int x2, int y2) {
        g.setColor(EDGE_COLOR);
        g.setStroke(new BasicStroke(1.8f));

        double dx = x2 - x1, dy = y2 - y1;
        double len = Math.sqrt(dx * dx + dy * dy);
        double ux = dx / len, uy = dy / len;
        int sx = (int) (x1 + ux * NODE_R);
        int sy = (int) (y1 + uy * NODE_R);
        int ex = (int) (x2 - ux * NODE_R);
        int ey = (int) (y2 - uy * NODE_R);
        g.drawLine(sx, sy, ex, ey);
    }

    /** Dibuja recursivamente cada nodo como un círculo con sombra, relleno, borde y su valor en el centro. */
    private void drawNodes(Graphics2D g, Node node, int y, int level) {
        if (node == null) return;

        boolean twoChildren = node.left != null && node.right != null;
        Color fill   = twoChildren ? NODE_FILL_2C : NODE_FILL;
        Color border = twoChildren ? NODE_BORDER_2C : NODE_BORDER;

        // Sombra
        g.setColor(new Color(0, 0, 0, 60));
        g.fill(new Ellipse2D.Float(node.x - NODE_R + 2, y - NODE_R + 2, NODE_R * 2, NODE_R * 2));

        // Relleno del círculo
        g.setColor(fill);
        g.fill(new Ellipse2D.Float(node.x - NODE_R, y - NODE_R, NODE_R * 2, NODE_R * 2));

        // Borde del círculo
        g.setColor(border);
        g.setStroke(new BasicStroke(twoChildren ? 2.2f : 1.5f));
        g.draw(new Ellipse2D.Float(node.x - NODE_R, y - NODE_R, NODE_R * 2, NODE_R * 2));

        // Valor centrado dentro del nodo
        String text = String.valueOf(node.value);
        g.setColor(TEXT_COLOR);
        g.setFont(new Font("Monospaced", Font.BOLD, text.length() > 3 ? 11 : 13));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(text,
            node.x - fm.stringWidth(text) / 2,
            y + fm.getAscent() / 2 - 1);

        drawNodes(g, node.left,  y + VERTICAL_GAP, level + 1);
        drawNodes(g, node.right, y + VERTICAL_GAP, level + 1);
    }

    /** Muestra un mensaje centrado cuando el árbol está vacío. */
    private void drawEmptyMessage(Graphics2D g) {
        g.setColor(EMPTY_COLOR);
        g.setFont(new Font("Monospaced", Font.ITALIC, 16));
        FontMetrics fm = g.getFontMetrics();
        String msg = "Árbol vacío — ingresa un valor e inserta";
        g.drawString(msg,
            (getWidth()  - fm.stringWidth(msg)) / 2,
            (getHeight() / 2));
    }
}