package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SelectionView extends JComponent {

    //region :: GLOBALS>>>>>>>>>
    private static final int DESIRED_WIDTH = 200;
    private static final int DESIRED_HEIGHT = 320;

    private final RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private final Color color;
    private final Image[] assets;
    public Image avatar;
    private final Image prevImg;
    private final Point iniPoint = new Point(0, 0);

    private final Rect[] buttons = new Rect[4];
    public String name;

    public boolean isComp, isPlayer = true, isNot;
    public boolean isFocus;
    private final FocusTeller focusTeller;
    private final Font font;

    //endregion :: GLOBALS>>>>>>>>>

    //region :: INIT_____

    public SelectionView(Color col, Image[] assets, Image avatar, FocusTeller teller) {
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        this.color = col;
        this.assets = assets;
        this.focusTeller = teller;
        this.avatar = avatar;
        this.prevImg = avatar;

        this.font = new Font("Ubuntu", Font.PLAIN, 22);
        for (int i = 0; i < 4; i++) {
            buttons[i] = new Rect();
        }

        buttons[0].setRect(20, 200, 40); // rect person
        buttons[1].setRect(80, 200, 40); // rect robot
        buttons[2].setRect(140, 200, 40); //rect cross
        buttons[3].top = 260;
        buttons[3].left = 20;
        buttons[3].bottom = 300;
        buttons[3].right = 180;


        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                for (byte i = 0; i < 4; i++) {
                    if (buttons[i].contains(e.getX() - iniPoint.x, e.getY() - iniPoint.y)) {
                        switch (i) {
                            case 0 -> {
                                isPlayer = true;
                                isComp = false;
                                isNot = false;
                            }
                            case 1 -> {
                                isComp = true;
                                isPlayer = false;
                                isNot = false;
                            }
                            case 2 -> {
                                isNot = true;
                                isComp = false;
                                isPlayer = false;
                            }
                            case 3 -> {
                                isFocus = true;
                                focusTeller.onFocusGained();
                            }
                        }
                        update();
                        break;
                    }
                }
            }
        });
    }

    //endregion :: INIT_____

    //region :: PAINTING_____

    @Override
    protected void paintComponent(Graphics __) {
        Graphics2D g = (Graphics2D) __;

        iniPoint.x = (getWidth() - DESIRED_WIDTH) >> 1;
        iniPoint.y = (getHeight() - DESIRED_HEIGHT) >> 1;

        g.setFont(font);
        g.setRenderingHints(hints);
        FontMetrics fm = g.getFontMetrics();

        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.WHITE);

        g.fillRoundRect(iniPoint.x, iniPoint.y, DESIRED_WIDTH, DESIRED_HEIGHT, 40, 40);

        g.drawImage(avatar, iniPoint.x + 60, iniPoint.y + 60, 80, 80, null);

        g.setColor(color);
        if (isComp)
            g.fillRoundRect(iniPoint.x + 74, iniPoint.y + 194, 52, 52, 20, 20);
        else if (isPlayer)
            g.fillRoundRect(iniPoint.x + 14, iniPoint.y + 194, 52, 52, 20, 20);


        g.drawImage(assets[0], iniPoint.x + 20, iniPoint.y + 200, 40, 40, null);
        g.drawImage(assets[1], iniPoint.x + 80, iniPoint.y + 200, 40, 40, null);
        g.drawImage(assets[2], iniPoint.x + 140, iniPoint.y + 200, 40, 40, null);

        g.fillRoundRect(iniPoint.x + 20, iniPoint.y + 260, 160, 40, 12, 12);

        g.setColor(Color.BLACK);
        g.drawString(name, iniPoint.x + 24, iniPoint.y + 290);

        if (isFocus)
            g.fillRoundRect(iniPoint.x + 24 + fm.stringWidth(name), iniPoint.y + 270, 2, 24, 2, 2);

        if (isNot) {
            g.setColor(new Color(0, 0, 0, 121));
            g.fillRect(0, 0, getWidth(), getHeight());
        }

    }

    public void update() {
        if (isComp) avatar = assets[1];
        if (isPlayer) avatar = prevImg;
        this.repaint();
    }

    //endregion :: PAINTING_____

    //region  :: COMMUNICATION_______

    public void setFocus() {
        isFocus = false;
        update();
    }

    public interface FocusTeller {
        void onFocusGained();
    }

    //endregion  :: COMMUNICATION_______

}
