import javax.swing.*;

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Circle okno = new Circle();
        okno.setVisible(true);
    });
}