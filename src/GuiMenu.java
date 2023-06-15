import ui.SelectionView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Random;

public class GuiMenu extends JFrame {

    private JPanel root;
    private JButton btStart;
    private JPanel pRed, pGreen, pYellow, pBlue;

    private SelectionView selR, selG, selY, selB;
    private final byte[] playing = {0, 0, 0, 0};


    //region :: INIT--->>

    public GuiMenu() {
        this.add(root);

        Image[] avatars = new Image[6];
        for (int i = 0; i < 6; i++)
            avatars[i] = new ImageIcon(Utils.path + "av" + (i + 1) + ".png").getImage();
        Image[] assets = new Image[3];
        assets[0] = new ImageIcon(Utils.path + "av1.png").getImage();
        assets[1] = new ImageIcon(Utils.path + "robot.png").getImage();
        assets[2] = new ImageIcon(Utils.path + "no.png").getImage();

        Random r = new Random(System.currentTimeMillis());

        SelectionView.FocusTeller red = () -> {
            selB.setFocus();
            selY.setFocus();
            selG.setFocus();
        };
        SelectionView.FocusTeller g = () -> {
            selB.setFocus();
            selR.setFocus();
            selY.setFocus();
        };
        SelectionView.FocusTeller y = () -> {
            selB.setFocus();
            selR.setFocus();
            selG.setFocus();
        };
        SelectionView.FocusTeller b = () -> {
            selR.setFocus();
            selY.setFocus();
            selG.setFocus();
        };

        selR = new SelectionView(new Color(243, 30, 30), assets, avatars[r.nextInt(6)], red);
        pRed.add(selR);
        selR.name = "Player1";

        selG = new SelectionView(new Color(16, 236, 33), assets, avatars[r.nextInt(6)], g);
        pGreen.add(selG);
        selG.name = "Player2";

        selY = new SelectionView(new Color(245, 210, 20), assets, avatars[r.nextInt(6)], y);
        pYellow.add(selY);
        selY.name = "Player3";

        selB = new SelectionView(new Color(9, 126, 231), assets, avatars[r.nextInt(6)], b);
        pBlue.add(selB);
        selB.name = "Player4";


        btStart.addActionListener((e) -> {

            checkPlayers(selR, 0);
            checkPlayers(selG, 1);
            checkPlayers(selY, 2);
            checkPlayers(selB, 3);

            String[] names = new String[4];
            names[0] = selR.name;
            names[1] = selG.name;
            names[2] = selY.name;
            names[3] = selB.name;

            byte numbers = 0;
            for (int i = 0; i < 4; i++)
                if (playing[i] != 0) numbers++;


            if (numbers < 2) return;

            selR.setFocus();
            selY.setFocus();
            selG.setFocus();
            selB.setFocus();

            Image[] avatarImg = new Image[4];
            avatarImg[0] = selR.avatar;
            avatarImg[1] = selG.avatar;
            avatarImg[2] = selY.avatar;
            avatarImg[3] = selB.avatar;

            startGame(names, avatarImg);

        });


        KeyboardFocusManager bfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        bfm.addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                if (e.getKeyChar() > 96 && e.getKeyChar() < 123) {
                    addNameChar(e.getKeyChar());
                } else if (e.getKeyChar() > 64 && e.getKeyChar() < 91) {
                    addNameChar(e.getKeyChar());
                } else if (e.getKeyChar() > 47 && e.getKeyChar() < 58) {
                    addNameChar(e.getKeyChar());
                } else if (e.getKeyChar() == 8) {
                    try {
                        backSpace();
                    } catch (Exception ignored) {
                    }
                } else if (e.getKeyChar() == 32) {
                    addNameChar(e.getKeyChar());
                    return true;
                }
            }
            return false;
        });
    }

    //endregion :: INIT---

    private void startGame(String[] names, Image[] avatarImg) {
        ActivityLudo first = new ActivityLudo(playing, names, avatarImg);
        first.setSize(1040, 800);
        first.setTitle("Simple Ludo by Himanshu...");
        first.setIconImage(new ImageIcon(Utils.path + "ludo.png").getImage());
        first.setVisible(true);
        first.setLocationRelativeTo(null);
        first.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    private void checkPlayers(SelectionView s, int pos) {
        if (s.isNot) {
            playing[pos] = 0;
        } else {
            if (s.isPlayer) playing[pos] = 1;
            else playing[pos] = 2;
        }
    }

    private void addNameChar(char c) {
        if (selR.isFocus) {
            selR.name += c;
            selR.update();
        }
        if (selG.isFocus) {
            selG.name += c;
            selG.update();
        }
        if (selB.isFocus) {
            selB.name += c;
            selB.update();
        }
        if (selY.isFocus) {
            selY.name += c;
            selY.update();
        }
    }

    private void backSpace() {

        if (selR.isFocus) {
            if (selR.name.startsWith("Player")) selR.name = "";
            else
                selR.name = selR.name.substring(0, selR.name.length() - 1);
            selR.update();
        }
        if (selG.isFocus) {
            if (selG.name.startsWith("Player")) selG.name = "";
            else
                selG.name = selG.name.substring(0, selG.name.length() - 1);
            selG.update();
        }
        if (selB.isFocus) {
            if (selB.name.startsWith("Player")) selB.name = "";
            else
                selB.name = selB.name.substring(0, selB.name.length() - 1);
            selB.update();
        }
        if (selY.isFocus) {
            if (selY.name.startsWith("Player")) selY.name = "";
            else
                selY.name = selY.name.substring(0, selY.name.length() - 1);
            selY.update();
        }
    }
}
