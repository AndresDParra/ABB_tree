package org.example;

import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GUIComposer gui = new GUIComposer();
            gui.setVisible(true);
        });
    }
}