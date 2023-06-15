import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

public class ActivityLudo extends JFrame implements LudoView.LudoLisner {
    private JPanel root, holder;
    private final LudoView view;

    private File soundRollDice, soundMovement, soundLoose, soundWin;

    public ActivityLudo(byte[] playing, String[] names, Image[] avatars) {
        this.add(root);

        try {
            soundLoose = new File(LudoView.PATH_SRC + "cut.wav");
            soundMovement = new File(LudoView.PATH_SRC + "move.wav");
            soundWin = new File(LudoView.PATH_SRC + "win.wav");
            soundRollDice = new File(LudoView.PATH_SRC + "die.wav");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        view = new LudoView(this, playing, names, avatars);
        holder.add(view);

        KeyboardFocusManager bfm = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        bfm.addKeyEventDispatcher(e -> {
            if (e.getID() == KeyEvent.KEY_PRESSED) {
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD1, KeyEvent.VK_1 -> view.setCheatingNo(1);
                    case KeyEvent.VK_NUMPAD2, KeyEvent.VK_2 -> view.setCheatingNo(2);
                    case KeyEvent.VK_NUMPAD3, KeyEvent.VK_3 -> view.setCheatingNo(3);
                    case KeyEvent.VK_NUMPAD4, KeyEvent.VK_4 -> view.setCheatingNo(4);
                    case KeyEvent.VK_NUMPAD5, KeyEvent.VK_5 -> view.setCheatingNo(5);
                    case KeyEvent.VK_NUMPAD6, KeyEvent.VK_6 -> view.setCheatingNo(6);
                }
            }
            return false;
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                view.gameOver = true;
            }
        });

    }

    private void playSound(File f, int sec) {
        try {
            Clip cli = AudioSystem.getClip();
            AudioInputStream ais = AudioSystem.getAudioInputStream(f);
            cli.open(ais);
            cli.start();
            new Thread(() -> {
                try {
                    Thread.sleep(sec);
                } catch (InterruptedException ignored) {
                }
                cli.stop();
                cli.flush();
                cli.close();
            }).start();
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onPlaySound(byte no) {
        try {
            switch (no) {
                case 1 -> playSound(soundRollDice, 2000);
                case 2 -> playSound(soundMovement, 1000);
                case 3 -> playSound(soundLoose, 2000);
                case 4 -> playSound(soundWin, 2600);
            }
        } catch (Exception ignored) {
        }
    }
}
