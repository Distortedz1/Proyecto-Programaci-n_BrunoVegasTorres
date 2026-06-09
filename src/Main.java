import view.Login;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
        } catch (Exception e) {
            System.out.println("Nimbus no disponible, usando el tema por defecto.");
        }

        SwingUtilities.invokeLater(() -> new Login());
    }
}
