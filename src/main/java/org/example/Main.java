package org.example;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Punto de entrada de la aplicación. Lanza la ventana principal en el hilo de eventos de Swing. */
public class Main {
    /** Inicia la interfaz gráfica aplicando el look and feel del sistema operativo. */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            GUIComposer frame = new GUIComposer();
            frame.setVisible(true);
        });
    }
}