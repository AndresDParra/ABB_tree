package org.example;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Ventana principal de la aplicación. Ensambla y muestra el panel del árbol y el panel de controles. */
public class GUIComposer extends JFrame {
    private static final Color LOBSTER_PINK = new Color(242, 84, 91);
    private static final Color CHERRY_ROSE  = new Color(169, 63, 85);
    private static final Color JET_BLACK    = new Color(25, 50, 60);
    private static final Color MINT_CREAM   = new Color(243, 247, 240);

    private TreePanel    treePanel;
    private ControlPanel controlPanel;
    private ABBTree      tree;

    /** Construye la ventana, crea el árbol compartido e inicializa todos los componentes visuales. */
    public GUIComposer() {
        setTitle("Árbol Binario de Búsqueda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 750);
        setMinimumSize(new java.awt.Dimension(800, 550));
        setLocationRelativeTo(null);
        setResizable(true);

        tree = new ABBTree();
        initializeComponents();
        setupLayout();
    }

    /** Crea las instancias de TreePanel y ControlPanel con el árbol compartido. */
    private void initializeComponents() {
        treePanel    = new TreePanel(tree);
        controlPanel = new ControlPanel(tree, treePanel);
    }

    /** Arma el layout: barra superior con título y un JSplitPane con el árbol a la izquierda y los controles a la derecha. */
    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CHERRY_ROSE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 2, 0, LOBSTER_PINK),
            BorderFactory.createEmptyBorder(6, 16, 6, 16)
        ));

        JLabel title = new JLabel("Árbol Binario de Búsqueda (ABB)", JLabel.CENTER);
        title.setForeground(MINT_CREAM);
        title.setFont(new Font("Monospaced", Font.BOLD, 17));
        topBar.add(title, BorderLayout.CENTER);

        JLabel hint = new JLabel("Enter para insertar  ·  Tooltip en cada botón", JLabel.RIGHT);
        hint.setForeground(new Color(210, 180, 175));
        hint.setFont(new Font("Monospaced", Font.PLAIN, 11));
        topBar.add(hint, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        JSplitPane split = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            treePanel,
            controlPanel
        );
        split.setResizeWeight(0.73);
        split.setDividerLocation(800);
        split.setDividerSize(4);
        split.setBorder(null);
        split.setEnabled(true);

        add(split, BorderLayout.CENTER);
        getContentPane().setBackground(JET_BLACK);
    }

    /** Permite lanzar la aplicación directamente desde esta clase. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            new GUIComposer().setVisible(true);
        });
    }
}