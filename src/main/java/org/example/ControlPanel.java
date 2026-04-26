package org.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

/** Panel lateral de controles. Contiene el campo de entrada, los botones de operaciones y el área de resultados. */
public class ControlPanel extends JPanel {
    private static final Color LOBSTER_PINK  = new Color(242, 84, 91);
    private static final Color CHERRY_ROSE   = new Color(169, 63, 85);
    private static final Color JET_BLACK     = new Color(25, 50, 60);
    private static final Color MINT_CREAM    = new Color(243, 247, 240);
    private static final Color SMOKY_ROSE    = new Color(140, 94, 88);
    private static final Color PANEL_BG      = new Color(32, 60, 72);
    private static final Color SECTION_BG    = new Color(20, 40, 50);
    private static final Color SUCCESS_GREEN = new Color(80, 200, 120);
    private static final Color WARN_YELLOW   = new Color(255, 200, 80);

    private JTextField inputField;
    private JTextArea  outputArea;
    private ABBTree    tree;
    private TreePanel  treePanel;

    // Listas de listeners externos para cada operación
    private List<ActionListener> insertListeners    = new ArrayList<>();
    private List<ActionListener> deleteListeners    = new ArrayList<>();
    private List<ActionListener> searchListeners    = new ArrayList<>();
    private List<ActionListener> inorderListeners   = new ArrayList<>();
    private List<ActionListener> preorderListeners  = new ArrayList<>();
    private List<ActionListener> postorderListeners = new ArrayList<>();
    private List<ActionListener> heightListeners    = new ArrayList<>();
    private List<ActionListener> clearListeners     = new ArrayList<>();

    /** Crea el panel de controles vinculado al árbol y al panel visual dados. */
    public ControlPanel(ABBTree tree, TreePanel treePanel) {
        this.tree      = tree;
        this.treePanel = treePanel;

        setLayout(new BorderLayout(0, 0));
        setPreferredSize(new Dimension(300, 600));
        setBackground(PANEL_BG);

        add(buildTopSection(),    BorderLayout.NORTH);
        add(buildOutputSection(), BorderLayout.CENTER);
    }

    /** Construye la sección superior con el campo de entrada y todas las filas de botones. */
    private JPanel buildTopSection() {
        JPanel top = new JPanel();
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        top.setBackground(PANEL_BG);
        top.setBorder(new EmptyBorder(12, 12, 8, 12));

        top.add(buildInputRow());
        top.add(Box.createVerticalStrut(10));
        top.add(buildSectionLabel("OPERACIONES"));
        top.add(Box.createVerticalStrut(6));
        top.add(buildOperationsRow());
        top.add(Box.createVerticalStrut(10));
        top.add(buildSectionLabel("RECORRIDOS"));
        top.add(Box.createVerticalStrut(6));
        top.add(buildTraversalsRow());
        top.add(Box.createVerticalStrut(10));
        top.add(buildSectionLabel("ÁRBOL"));
        top.add(Box.createVerticalStrut(6));
        top.add(buildTreeInfoRow());

        return top;
    }

    /** Construye la fila con el label "Valor" y el campo de texto. Presionar Enter dispara la inserción. */
    private JPanel buildInputRow() {
        JPanel row = new JPanel(new BorderLayout(8, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));

        JLabel lbl = new JLabel("Valor");
        lbl.setForeground(new Color(180, 200, 210));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 11));
        lbl.setPreferredSize(new Dimension(42, 28));

        inputField = new JTextField();
        inputField.setBackground(SECTION_BG);
        inputField.setForeground(MINT_CREAM);
        inputField.setCaretColor(LOBSTER_PINK);
        inputField.setSelectionColor(LOBSTER_PINK);
        inputField.setSelectedTextColor(MINT_CREAM);
        inputField.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(SMOKY_ROSE, 1),
            new EmptyBorder(4, 8, 4, 8)
        ));
        inputField.addActionListener(e -> handleInsert());

        row.add(lbl,        BorderLayout.WEST);
        row.add(inputField, BorderLayout.CENTER);
        return row;
    }

    /** Crea un label de encabezado centrado para separar visualmente las secciones de botones. */
    private JLabel buildSectionLabel(String text) {
        JLabel lbl = new JLabel(text, JLabel.CENTER);
        lbl.setForeground(new Color(140, 160, 170));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 10));
        lbl.setAlignmentX(Component.CENTER_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 16));
        return lbl;
    }

    /** Construye la fila de botones Insertar, Eliminar y Buscar. */
    private JPanel buildOperationsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton ins = makeBtn("Insertar", LOBSTER_PINK);
        JButton del = makeBtn("Eliminar", CHERRY_ROSE);
        JButton src = makeBtn("Buscar",   SMOKY_ROSE);

        ins.addActionListener(e -> { fireInsertEvent(e); handleInsert(); });
        del.addActionListener(e -> { fireDeleteEvent(e); handleDelete(); });
        src.addActionListener(e -> { fireSearchEvent(e); handleSearch(); });

        row.add(ins); row.add(del); row.add(src);
        return row;
    }

    /** Construye la fila de botones Inorden, Preorden y Postorden. */
    private JPanel buildTraversalsRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton ino  = makeBtn("Inorden",   new Color(80, 140, 180));
        JButton pre  = makeBtn("Preorden",  new Color(60, 120, 160));
        JButton post = makeBtn("Postorden", new Color(40, 100, 140));

        ino.addActionListener(e  -> { fireInorderEvent(e);   handleInorder();   });
        pre.addActionListener(e  -> { firePreorderEvent(e);  handlePreorder();  });
        post.addActionListener(e -> { firePostorderEvent(e); handlePostorder(); });

        row.add(ino); row.add(pre); row.add(post);
        return row;
    }

    /** Construye la fila de botones Altura, Nivel y Limpiar. */
    private JPanel buildTreeInfoRow() {
        JPanel row = new JPanel(new GridLayout(1, 3, 6, 0));
        row.setBackground(PANEL_BG);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));

        JButton hgt = makeBtn("Altura",  new Color(100, 160, 100));
        JButton lvl = makeBtn("Nivel",   new Color(80,  130, 170));
        JButton clr = makeBtn("Limpiar", new Color(160, 80,  60));

        hgt.addActionListener(e -> { fireHeightEvent(e); handleHeight(); });
        lvl.addActionListener(e -> handleNivel());
        clr.addActionListener(e -> { fireClearEvent(e);  handleClear();  });

        row.add(hgt); row.add(lvl); row.add(clr);
        return row;
    }

    /** Construye el área de resultado con su scroll y etiqueta de sección. */
    private JPanel buildOutputSection() {
        JPanel wrapper = new JPanel(new BorderLayout(0, 6));
        wrapper.setBackground(PANEL_BG);
        wrapper.setBorder(new EmptyBorder(0, 12, 12, 12));

        JLabel lbl = new JLabel("RESULTADO");
        lbl.setForeground(new Color(140, 160, 170));
        lbl.setFont(new Font("Monospaced", Font.BOLD, 10));

        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setBackground(SECTION_BG);
        outputArea.setForeground(new Color(180, 230, 200));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        outputArea.setBorder(new EmptyBorder(8, 10, 8, 10));
        outputArea.setCaretColor(LOBSTER_PINK);

        JScrollPane scroll = new JScrollPane(outputArea);
        scroll.setBorder(BorderFactory.createLineBorder(SMOKY_ROSE, 1));
        scroll.setBackground(SECTION_BG);
        scroll.getViewport().setBackground(SECTION_BG);

        wrapper.add(lbl,    BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    /** Crea un botón estilizado con esquinas redondeadas y efecto hover/press usando el color de acento dado. */
    private JButton makeBtn(String label, Color accent) {
        JButton btn = new JButton(label) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed()
                    ? accent.darker()
                    : getModel().isRollover()
                        ? accent.brighter()
                        : accent;
                g2.setColor(bg);
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 8, 8));
                g2.dispose();
                super.paintComponent(g);
            }
            @Override protected void paintBorder(Graphics g) {}
        };
        btn.setForeground(MINT_CREAM);
        btn.setFont(new Font("Monospaced", Font.BOLD, 11));
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(4, 6, 4, 6));
        btn.setPreferredSize(new Dimension(80, 28));
        String tip = getTooltip(label);
        if (tip != null) btn.setToolTipText(tip);
        return btn;
    }

    /** Retorna el texto del tooltip para cada botón según su etiqueta. */
    private String getTooltip(String label) {
        return switch (label) {
            case "Insertar"  -> "Insertar valor (Enter)";
            case "Eliminar"  -> "Eliminar valor del árbol";
            case "Buscar"    -> "Buscar valor en el árbol";
            case "Inorden"   -> "Izquierda → Raíz → Derecha";
            case "Preorden"  -> "Raíz → Izquierda → Derecha";
            case "Postorden" -> "Izquierda → Derecha → Raíz";
            case "Altura"    -> "Mostrar altura del árbol";
            case "Nivel"     -> "Nivel del valor ingresado (raíz = 0)";
            case "Limpiar"   -> "Vaciar todo el árbol";
            default          -> null;
        };
    }

    /** Lee el texto del campo de entrada y lo retorna sin espacios. */
    private String getInput() {
        return inputField.getText().trim();
    }

    /** Inserta el valor del campo en el árbol y muestra el nuevo tamaño y altura. */
    private void handleInsert() {
        try {
            int val = Integer.parseInt(getInput());
            tree.insert(val);
            treePanel.repaint();
            showOutput("✓ Insertado: " + val
                + "\n  Tamaño: " + tree.getSize()
                + "  |  Altura: " + tree.getHeight(), SUCCESS_GREEN);
            inputField.setText("");
        } catch (NumberFormatException ex) {
            showOutput("✗ Ingresa un número entero válido.", LOBSTER_PINK);
        }
    }

    /** Elimina el valor del campo del árbol. Avisa si el valor no existe. */
    private void handleDelete() {
        try {
            int val = Integer.parseInt(getInput());
            if (!tree.search(val)) {
                showOutput("✗ El valor " + val + " no existe en el árbol.", WARN_YELLOW);
                return;
            }
            tree.delete(val);
            treePanel.repaint();
            showOutput("✓ Eliminado: " + val
                + "\n  Tamaño: " + tree.getSize()
                + "  |  Altura: " + tree.getHeight(), SUCCESS_GREEN);
            inputField.setText("");
        } catch (NumberFormatException ex) {
            showOutput("✗ Ingresa un número entero válido.", LOBSTER_PINK);
        }
    }

    /** Busca el valor del campo en el árbol y muestra si fue encontrado o no. */
    private void handleSearch() {
        try {
            int val = Integer.parseInt(getInput());
            boolean found = tree.search(val);
            if (found) {
                showOutput("✓ Encontrado: " + val + " está en el árbol.", SUCCESS_GREEN);
            } else {
                showOutput("✗ No encontrado: " + val + " no está en el árbol.", WARN_YELLOW);
            }
        } catch (NumberFormatException ex) {
            showOutput("✗ Ingresa un número entero válido.", LOBSTER_PINK);
        }
    }

    /** Muestra los valores del árbol en recorrido inorden (orden ascendente). */
    private void handleInorder() {
        String result = tree.inorderString();
        showOutput("Inorden:\n  " + (result.isEmpty() ? "(vacío)" : result),
            new Color(120, 190, 230));
    }

    /** Muestra los valores del árbol en recorrido preorden. */
    private void handlePreorder() {
        String result = tree.preorderString();
        showOutput("Preorden:\n  " + (result.isEmpty() ? "(vacío)" : result),
            new Color(120, 190, 230));
    }

    /** Muestra los valores del árbol en recorrido postorden. */
    private void handlePostorder() {
        String result = tree.postorderString();
        showOutput("Postorden:\n  " + (result.isEmpty() ? "(vacío)" : result),
            new Color(120, 190, 230));
    }

    /** Muestra la altura, cantidad de nodos, hojas, valor mínimo y máximo del árbol. */
    private void handleHeight() {
        if (tree.isEmpty()) {
            showOutput("El árbol está vacío.", WARN_YELLOW);
            return;
        }
        showOutput("Altura: " + tree.getHeight()
            + "\nNodos:  " + tree.getSize()
            + "\nHojas:  " + tree.countLeaves()
            + "\nMínimo: " + tree.getMinValue()
            + "\nMáximo: " + tree.getMaxValue(),
            new Color(100, 200, 150));
    }

    /** Muestra el nivel en que se encuentra el valor ingresado dentro del árbol. */
    private void handleNivel() {
        try {
            int val = Integer.parseInt(getInput());
            if (tree.isEmpty()) {
                showOutput("El árbol está vacío.", WARN_YELLOW);
                return;
            }
            int nivel = tree.getLevel(val);
            if (nivel == -1) {
                showOutput("✗ El valor " + val + " no está en el árbol.", WARN_YELLOW);
            } else {
                showOutput("Nivel de " + val + ": " + nivel
                    + (nivel == 0 ? "  (es la raíz)" : ""), new Color(100, 200, 150));
            }
        } catch (NumberFormatException ex) {
            showOutput("✗ Ingresa un número entero válido.", LOBSTER_PINK);
        }
    }

    /** Limpia todos los nodos del árbol y actualiza el panel visual. */
    private void handleClear() {
        tree.clear();
        treePanel.repaint();
        showOutput("Árbol limpiado.", WARN_YELLOW);
    }

    /** Actualiza el área de resultado con el texto y color indicados. */
    private void showOutput(String text, Color color) {
        outputArea.setForeground(color);
        outputArea.setText(text);
    }

    // Notifican a los listeners externos registrados para cada operación
    private void fireInsertEvent(java.awt.event.ActionEvent e)    { insertListeners.forEach(l    -> l.actionPerformed(e)); }
    private void fireDeleteEvent(java.awt.event.ActionEvent e)    { deleteListeners.forEach(l    -> l.actionPerformed(e)); }
    private void fireSearchEvent(java.awt.event.ActionEvent e)    { searchListeners.forEach(l    -> l.actionPerformed(e)); }
    private void fireInorderEvent(java.awt.event.ActionEvent e)   { inorderListeners.forEach(l   -> l.actionPerformed(e)); }
    private void firePreorderEvent(java.awt.event.ActionEvent e)  { preorderListeners.forEach(l  -> l.actionPerformed(e)); }
    private void firePostorderEvent(java.awt.event.ActionEvent e) { postorderListeners.forEach(l -> l.actionPerformed(e)); }
    private void fireHeightEvent(java.awt.event.ActionEvent e)    { heightListeners.forEach(l    -> l.actionPerformed(e)); }
    private void fireClearEvent(java.awt.event.ActionEvent e)     { clearListeners.forEach(l     -> l.actionPerformed(e)); }

    // Permiten registrar listeners externos para cada operación del árbol
    public void addInsertListener(ActionListener l)    { insertListeners.add(l);    }
    public void addDeleteListener(ActionListener l)    { deleteListeners.add(l);    }
    public void addSearchListener(ActionListener l)    { searchListeners.add(l);    }
    public void addInorderListener(ActionListener l)   { inorderListeners.add(l);   }
    public void addPreorderListener(ActionListener l)  { preorderListeners.add(l);  }
    public void addPostorderListener(ActionListener l) { postorderListeners.add(l); }
    public void addHeightListener(ActionListener l)    { heightListeners.add(l);    }
    public void addClearListener(ActionListener l)     { clearListeners.add(l);     }

    /** Retorna el campo de texto de entrada (para acceso externo si se necesita). */
    public JTextField getInputField() { return inputField; }
}