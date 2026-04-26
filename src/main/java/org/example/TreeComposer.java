package org.example;

import javax.swing.*;
import java.awt.*;

public class TreeComposer extends JFrame {
    private static final Color LOBSTER_PINK = new Color(242, 84, 91);
    private static final Color CHERRY_ROSE = new Color(169, 63, 85);
    private static final Color JET_BLACK = new Color(25, 50, 60);
    private static final Color MINT_CREAM = new Color(243, 247, 240);
    private static final Color SMOKY_ROSE = new Color(140, 94, 88);

    public TreeComposer() {
        setTitle("Visualizador de Árbol Binario de Búsqueda");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        ABBTree tree = new ABBTree();
        TreePanel treePanel = new TreePanel(tree);
        ControlPanel controlPanel = new ControlPanel(tree, treePanel);
        
        setLayout(new BorderLayout());
        
        // Panel superior con título
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(CHERRY_ROSE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, LOBSTER_PINK));
        
        JLabel titleLabel = new JLabel("Árbol Binario de Búsqueda", JLabel.CENTER);
        titleLabel.setForeground(MINT_CREAM);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        topPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        
        // Panel principal dividido
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treePanel, controlPanel);
        splitPane.setDividerLocation(600);
        splitPane.setEnabled(false); // Deshabilitar redimensionamiento manual
        
        add(splitPane, BorderLayout.CENTER);
        
        // Establecer tamaño y centrar ventana
        setSize(900, 700);
        setLocationRelativeTo(null);
        getContentPane().setBackground(JET_BLACK);
    }
}