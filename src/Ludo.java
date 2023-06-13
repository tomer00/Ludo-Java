import javax.swing.*;

public class Ludo {
    public static void main(String[] args) {
        GuiMenu menu = new GuiMenu();
        menu.setIconImage(new ImageIcon(Utils.path+"ludoLogo.png").getImage());
        menu.setSize(1000,600);
        menu.setTitle("Ludo by Himanshu Tomer");
        menu.setLocationRelativeTo(null);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }
}