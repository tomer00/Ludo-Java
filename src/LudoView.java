import ui.Rect;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileInputStream;
import java.util.Random;

public class LudoView extends JComponent {

    //region GLOBALS>>>>>

    public static final int CELL_SIZE = 46;
    private static final int SIZE = 690;
    public static final String PATH_SRC = "/home/tom/Desktop/Python/src/ludo/";
    public static final PointByte[][] mappings = new PointByte[4][61];

    private final byte[][] goti = new byte[4][4]; // tracks the -6---->56 , 57--->60 position of goti on the board....
    private final Point[][] gotiPos = new Point[4][4]; //holds the actual drawing pos on canvas....
    private final Point[][] posHome = new Point[4][4];
    private final Color[] colors = new Color[4]; // colors for each indications....

    private final byte[] currentWins = new byte[4];
    public boolean gameOver = false;
    private byte currentWinningPos = 0;

    private final Image[] die = new Image[6], imgGoti = new Image[4], imgWinnings = new Image[4], imgFrames = new Image[8];
    private final Image imgBoard, imgHighLighter, imgOver, imgWinBg;
    private final Point initialPoint = new Point(0, 0);
    private final RenderingHints hints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

    private byte currColTurn = -1, currDieNO = 1;
    private final Rect[] clicablePositions = new Rect[4];
    private final Rect dieRect = new Rect();
    private final Point[] safePositions = new Point[8];

    private final Random random = new Random(System.currentTimeMillis());
    private final LudoLisner lisner;
    private byte cheatingNo = -1;
    private boolean dieRolling = false;
    private int frames = 0;

    private final byte[] playing;
    private final String[] names;
    private final Image[] avatar;
    private final Font font = new Font("Ubuntu", Font.PLAIN, 22);

    //endregion GLOBALS>>>>>

    //region INITIALIZER

    public LudoView(LudoLisner lisner, byte[] playing, String[] names, Image[] avatars) {
        this.lisner = lisner;
        this.playing = playing;
        this.names = names;
        this.avatar = avatars;

        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        imgGoti[0] = new ImageIcon(PATH_SRC + "pRed.png").getImage();
        imgGoti[3] = new ImageIcon(PATH_SRC + "pBlue.png").getImage();
        imgGoti[2] = new ImageIcon(PATH_SRC + "pYellow.png").getImage();
        imgGoti[1] = new ImageIcon(PATH_SRC + "pGreen.png").getImage();
        imgBoard = new ImageIcon(PATH_SRC + "board.jpg").getImage();
        imgOver = new ImageIcon(PATH_SRC + "over.png").getImage();
        imgHighLighter = new ImageIcon(PATH_SRC + "gra2.png").getImage();
        imgWinBg = new ImageIcon(PATH_SRC + "gra1.png").getImage();
        imgWinnings[3] = new ImageIcon(PATH_SRC + "no.png").getImage();
        imgWinnings[0] = new ImageIcon(PATH_SRC + "win1.png").getImage();
        imgWinnings[1] = new ImageIcon(PATH_SRC + "win2.png").getImage();
        imgWinnings[2] = new ImageIcon(PATH_SRC + "win3.png").getImage();

        colors[0] = new Color(243, 30, 30);
        colors[1] = new Color(16, 236, 33);
        colors[2] = new Color(245, 210, 20);
        colors[3] = new Color(9, 126, 231);

        for (int i = 0; i < 6; i++)
            die[i] = new ImageIcon(PATH_SRC + "d" + (i + 1) + ".png").getImage();

        for (int i = 0; i < 8; i++)
            imgFrames[i] = new ImageIcon(PATH_SRC + "f" + (i + 1) + ".png").getImage();

        for (int i = 0; i < 4; i++) {
            if (playing[i] == 0) currentWins[i] = 4;
            else currentWins[i] = 0;
        }

        setNextColTurn();

        MouseListener mouseListener = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (dieRect.contains(e.getX() - initialPoint.x, e.getY() - initialPoint.y)) {
                    if (dieRolling) return;
                    lisner.onPlaySound((byte) 1);
                    new Thread(() -> rollDie()).start();
                    return;
                }
                for (byte i = 0; i < 4; i++) {
                    if (clicablePositions[i].contains(e.getX() - initialPoint.x, e.getY() - initialPoint.y)) {
                        gotiChalo(i);
                        break;
                    }
                }
            }

        };
        this.addMouseListener(mouseListener);

        //region Goti positons inintializations

        for (byte i = 0; i < 4; i++) {
            clicablePositions[i] = new Rect();
            clicablePositions[i].setRect(-1000, -1000, 0);
            for (byte j = 0; j < 4; j++)
                goti[j][i] = -6;
        }

        safePositions[0] = new Point(276, 92);
        safePositions[1] = new Point(368, 46);
        safePositions[2] = new Point(552, 276);
        safePositions[3] = new Point(598, 368);
        safePositions[4] = new Point(368, 552);
        safePositions[5] = new Point(276, 598);
        safePositions[6] = new Point(92, 368);
        safePositions[7] = new Point(46, 276);

        gotiPos[0][0] = new Point(114, 49); // red ki first goti ki position
        gotiPos[0][1] = new Point(59, 104);
        gotiPos[0][2] = new Point(170, 104);
        gotiPos[0][3] = new Point(114, 160);

        gotiPos[1][0] = new Point(528, 49); // green ki first goti ki position
        gotiPos[1][1] = new Point(473, 104);
        gotiPos[1][2] = new Point(584, 104);
        gotiPos[1][3] = new Point(528, 160);

        gotiPos[2][0] = new Point(528, 463); // yellow ki first goti ki position
        gotiPos[2][1] = new Point(473, 518);
        gotiPos[2][2] = new Point(584, 518);
        gotiPos[2][3] = new Point(528, 574);

        gotiPos[3][0] = new Point(114, 463); // blue ki first goti ki position
        gotiPos[3][1] = new Point(59, 518);
        gotiPos[3][2] = new Point(170, 518);
        gotiPos[3][3] = new Point(114, 574);

        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                posHome[i][j] = new Point();
                posHome[i][j].x = gotiPos[i][j].x;
                posHome[i][j].y = gotiPos[i][j].y;
            }


        //endregion Goti positions initializations

        //initialize mappings.....

        try {
            final byte[] loc = new byte[2];
            FileInputStream inputStream = new FileInputStream(PATH_SRC + "data");

            for (byte j = 0; j < 4; j++)
                for (byte i = 0; i < 61; i++) {
                    inputStream.read(loc);
                    mappings[j][i] = new PointByte(loc[0], loc[1]);
                }

            inputStream.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //mapping done...

        dieRect.setRect(740, 360, 64);

    }

    @Override
    protected void paintComponent(Graphics g) {
        initialPoint.x = ((getWidth() - SIZE) >> 1) - 60;
        initialPoint.y = ((getHeight() - SIZE) >> 1);
        draw(g);
    }

    //endregion INITIALIZER

    //region MouseAdapter

    private void update() {
        this.repaint();
    }

    private void dieWithHigh6() {
        int i = random.nextInt(8);
        if (i > 5) currDieNO = 5;
        else currDieNO = (byte) i;
    }

    private void rollDie() {

        //if screen is closed by user
        if (gameOver) return;

        dieRect.setRect(0, 0, 0);
        dieRolling = true;
        byte n = 0;
        while (n < 20) {
            update();
            n++;
            try {
                Thread.sleep(60);
            } catch (InterruptedException ignored) {
            }
        }
        currDieNO = (byte) random.nextInt(6);
        byte gotiBhar = 0;
        for (int i = 0; i < 4; i++)
            if (goti[currColTurn][i] > -1 && goti[currColTurn][i] < 57) gotiBhar++;
        if (gotiBhar == 0)
            dieWithHigh6();
        if (cheatingNo != -1) currDieNO = (byte) (cheatingNo - 1);
        dieRolled();
    }

    private void animateGoti(byte n, byte gotiNo) {
        for (int i = 0; i < n; i++) {
            try {
                placeGoti((byte) (goti[currColTurn][gotiNo] + 1), gotiNo);
                update();
                Thread.sleep(200);
            } catch (Exception ignored) {
            }
        }

        // GOTI PAK GYI......
        byte pos = (byte) ((goti[currColTurn][gotiNo]) + 1);
        if (pos == 56) {
            pos = (byte) (57 + gotiNo);
            placeGoti(pos, gotiNo);

            //condition for winning

            if (goti[currColTurn][0] > 56 && goti[currColTurn][1] > 56 && goti[currColTurn][2] > 56 && goti[currColTurn][3] > 56) {
                // current col wins
                currentWins[currColTurn] = ++currentWinningPos;
                lisner.onPlaySound((byte) 4);

                // condition for game over...
                byte count = 0;
                for (byte i = 0; i < 4; i++)
                    if (currentWins[i] > 0) count++;


                if (count == 3) { //Game Over
                    gameOver = true;
                    dieRect.setRect(-1000, -1000, 0);
                } else setNextColTurn();
            } else {
                if (playing[currColTurn] == 2) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        lisner.onPlaySound((byte) 1);
                        rollDie();

                    }).start();
                } else
                    dieRect.setRect(740, 360, 64);
            }
        } else {

            placeGoti(pos, gotiNo);
            for (int i = 0; i < 4; i++) {
                if (i == currColTurn) continue;
                for (int j = 0; j < 4; j++) {
                    if (gotiPos[currColTurn][gotiNo].x == gotiPos[i][j].x && gotiPos[currColTurn][gotiNo].y == gotiPos[i][j].y) {
                        for (int k = 0; k < 8; k++)
                            if (gotiPos[currColTurn][gotiNo].x == safePositions[k].x && gotiPos[currColTurn][gotiNo].y == safePositions[k].y) {
                                if (currDieNO != 5) setNextColTurn();
                                else {
                                    if (playing[currColTurn] == 2) {
                                        new Thread(() -> {
                                            try {
                                                Thread.sleep(1000);
                                            } catch (InterruptedException ignored) {
                                            }
                                            lisner.onPlaySound((byte) 1);
                                            rollDie();
                                        }).start();
                                    } else
                                        dieRect.setRect(740, 360, 64);
                                }
                                update();
                                return;
                            }
                        // GOTI CUT GYI

                        if (playing[currColTurn] == 2) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                                lisner.onPlaySound((byte) 1);
                                rollDie();
                            }).start();
                        } else
                            dieRect.setRect(740, 360, 64);


                        lisner.onPlaySound((byte) 3);
                        gotiPos[i][j].x = posHome[i][j].x;
                        gotiPos[i][j].y = posHome[i][j].y;
                        goti[i][j] = -6;
                        update();
                        return;
                    }
                }
            }

            if (currDieNO != 5) setNextColTurn();
            else {
                if (playing[currColTurn] == 2) {
                    new Thread(() -> {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        lisner.onPlaySound((byte) 1);
                        rollDie();
                    }).start();
                } else
                    dieRect.setRect(740, 360, 64);
            }
        }
        update();
    }

    private void placeGoti(byte pos, byte gotiNo) {
        lisner.onPlaySound((byte) 2);
        goti[currColTurn][gotiNo] = pos;
        gotiPos[currColTurn][gotiNo].x = mappings[currColTurn][pos].x * CELL_SIZE;
        gotiPos[currColTurn][gotiNo].y = mappings[currColTurn][pos].y * CELL_SIZE;
    }

    //endregion MouseAdapter

    private void gotiChalo(byte i) {
        if (goti[currColTurn][i] == -6) {
            goti[currColTurn][i] = 0;
            gotiPos[currColTurn][i].x = mappings[currColTurn][0].x * CELL_SIZE;
            gotiPos[currColTurn][i].y = mappings[currColTurn][0].y * CELL_SIZE;
            dieRect.setRect(740, 360, 64);
        } else {
            new Thread(() -> animateGoti(currDieNO, i)).start();
        }
        for (int j = 0; j < 4; j++) clicablePositions[j].setRect(-1000, -1000, 0);
        update();
    }

    //region DRAWING ------>

    private void draw(Graphics __) {
        Graphics2D g = (Graphics2D) __;
        g.setRenderingHints(hints);

        g.setColor(Color.darkGray);
        g.fillRoundRect(initialPoint.x + 640, initialPoint.y + 200, 220, 320, 32, 32);

        g.drawImage(imgBoard, initialPoint.x, initialPoint.y, SIZE, SIZE, null);

        for (byte i = 0; i < 4; i++) {
            if (i != currColTurn) {
                for (byte j = 0; j < 4; j++)
                    g.drawImage(imgGoti[i], gotiPos[i][j].x + initialPoint.x, gotiPos[i][j].y + initialPoint.y, CELL_SIZE, CELL_SIZE, null);
            }
        }

        for (byte j = 0; j < 4; j++)
            g.drawImage(imgHighLighter, clicablePositions[j].left + initialPoint.x, clicablePositions[j].top + initialPoint.y, CELL_SIZE, CELL_SIZE, null);

        for (byte j = 0; j < 4; j++)
            g.drawImage(imgGoti[currColTurn], gotiPos[currColTurn][j].x + initialPoint.x, gotiPos[currColTurn][j].y + initialPoint.y, CELL_SIZE, CELL_SIZE, null);


        // die and info printing.........


        if (!dieRolling)
            g.drawImage(die[currDieNO], initialPoint.x + 740, initialPoint.y + 360, 64, 64, null, null);
        else
            g.drawImage(imgFrames[frames++ % 8], initialPoint.x + 720, initialPoint.y + 340, 104, 104, null, null);

        g.setColor(Color.WHITE);
        g.fillOval(initialPoint.x + 748, initialPoint.y + 296, 48, 48);

        g.setColor(colors[currColTurn]);
        g.fillOval(initialPoint.x + 752, initialPoint.y + 300, 40, 40);

        //region Handlng the drawing of Winnings and not playin

        if (gameOver)
            g.drawImage(imgOver, initialPoint.x + CELL_SIZE + mappings[0][59].x * CELL_SIZE, initialPoint.y + CELL_SIZE + mappings[0][59].y * CELL_SIZE, 3 * CELL_SIZE, 3 * CELL_SIZE, null);

        for (int i = 0; i < 4; i++)
            if (currentWins[i] > 0) {
                if (currentWins[i] != 4)
                    g.drawImage(imgWinBg, initialPoint.x + mappings[i][57].x * CELL_SIZE, initialPoint.y + mappings[i][57].y * CELL_SIZE, 6 * CELL_SIZE, 6 * CELL_SIZE, null);
                g.drawImage(imgWinnings[currentWins[i] - 1], initialPoint.x + mappings[i][57].x * CELL_SIZE, initialPoint.y + mappings[i][57].y * CELL_SIZE, 6 * CELL_SIZE, 6 * CELL_SIZE, null);
            }

        //endregion Handlng the drawing of Winnings and not playin

        switch (currColTurn) {
            case 0 -> g.drawImage(imgHighLighter, initialPoint.x - 100, initialPoint.y - 10, 100, 100, null); // REd
            case 1 -> g.drawImage(imgHighLighter, initialPoint.x + SIZE, initialPoint.y - 10, 100, 100, null); // Green
            case 2 ->
                    g.drawImage(imgHighLighter, initialPoint.x + SIZE, initialPoint.y + SIZE - 90, 100, 100, null); // Yellow
            case 3 ->
                    g.drawImage(imgHighLighter, initialPoint.x - 100, initialPoint.y + SIZE - 90, 100, 100, null); // Blue
        }

        if (playing[0] != 0)
            g.drawImage(avatar[0], initialPoint.x - 90, initialPoint.y, 80, 80, null); // REd
        if (playing[1] != 0)
            g.drawImage(avatar[1], initialPoint.x + SIZE + 10, initialPoint.y, 80, 80, null); // Green
        if (playing[2] != 0)
            g.drawImage(avatar[2], initialPoint.x + SIZE + 10, initialPoint.y + SIZE - 80, 80, 80, null); // Yellow
        if (playing[3] != 0)
            g.drawImage(avatar[3], initialPoint.x - 90, initialPoint.y + SIZE - 80, 80, 80, null); // Blue


        g.setColor(Color.BLACK);
        g.setFont(font);
        FontMetrics met = g.getFontMetrics();
        if (playing[0] != 0)
            g.drawString(names[0], initialPoint.x, initialPoint.y - 12);
        if (playing[1] != 0)
            g.drawString(names[1], initialPoint.x + SIZE - met.stringWidth(names[1]), initialPoint.y - 12);
        if (playing[2] != 0)
            g.drawString(names[2], initialPoint.x + SIZE - met.stringWidth(names[1]), initialPoint.y + SIZE + met.getHeight());
        if (playing[3] != 0)
            g.drawString(names[3], initialPoint.x, initialPoint.y + SIZE + met.getHeight());

    }

    //endregion DRAWING ------>

    private void dieRolled() {

        byte noOfClickableGoti = 0;
        byte gotNo = 0;
        for (byte i = 0; i < 4; i++) {
            int index = goti[currColTurn][i] + currDieNO + 1;
            if (index > -1 && index < 57) {
                noOfClickableGoti++;
                gotNo = i;
                clicablePositions[i].setRect(gotiPos[currColTurn][i].x, gotiPos[currColTurn][i].y, CELL_SIZE);
            } else clicablePositions[i].setRect(-1000, -1000, 0);
        }


        if (noOfClickableGoti == 1) {
            byte finalGotiNo = gotNo;
            new Thread(() -> animateGoti(currDieNO, finalGotiNo)).start();
            for (int j = 0; j < 4; j++) clicablePositions[j].setRect(-1000, -1000, 0);
            update();
        } else {
            if (noOfClickableGoti == 0) setNextColTurn();
            else {
                if (playing[currColTurn] == 2) {
                    byte g = gotino((byte) (currDieNO + 1), currColTurn);
                    if (goti[currColTurn][g] == -6) {
                        goti[currColTurn][g] = 0;
                        gotiPos[currColTurn][g].x = mappings[currColTurn][0].x * CELL_SIZE;
                        gotiPos[currColTurn][g].y = mappings[currColTurn][0].y * CELL_SIZE;

                        new Thread(() -> {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ignored) {
                            }
                            lisner.onPlaySound((byte) 1);
                            rollDie();

                        }).start();

                    } else
                        new Thread(() -> animateGoti(currDieNO, g)).start();

                    for (int j = 0; j < 4; j++) clicablePositions[j].setRect(-1000, -1000, 0);
                }
            }
        }
        dieRolling = false;
        cheatingNo = -1;
        update();
    }

    private void setNextColTurn() {
        dieRect.setRect(740, 360, 64);
        currColTurn++;
        currColTurn = (byte) (currColTurn % 4);
        if (currentWins[currColTurn] > 0) {
            setNextColTurn();
            return;
        }

        if (playing[currColTurn] != 2) return;
        dieRect.setRect(0, 0, 0);
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
            lisner.onPlaySound((byte) 1);
            rollDie();

        }).start();
    }

    //region Communicatioin

    public void setCheatingNo(int no) {
        this.cheatingNo = (byte) no;
    }

    //endregion Communicatioin

    //region _____BOT FOR COMPUTER_______

    public byte gotino(byte currDieNO, byte currColTurn) {

        byte goti = 0, loc = -1;
        // 6 aagya phle dekho koi kat to nhi rhi

        for (byte gotiNo = 0; gotiNo < 4; gotiNo++) {

            byte newPos = (byte) (this.goti[currColTurn][gotiNo] + currDieNO);

            if (newPos > loc && newPos < 57) {
                goti = gotiNo;
                loc = newPos;
            }

            if (newPos > -1 && newPos < 52) {
                Point newLoc = new Point(mappings[currColTurn][newPos].x * CELL_SIZE, mappings[currColTurn][newPos].y * CELL_SIZE);
                for (int i = 0; i < 4; i++) {
                    if (i == currColTurn) continue;
                    for (int j = 0; j < 4; j++) {
                        if (newLoc.x == gotiPos[i][j].x && newLoc.y == gotiPos[i][j].y)
                            return gotiNo;
                    }
                }
            }
        }
        if (currDieNO == 6) {
            byte gotiBhar = 0;
            for (int i = 0; i < 4; i++) {
                if (this.goti[currColTurn][i] > -1 && this.goti[currColTurn][i] < 52) gotiBhar++;
            }
            if (gotiBhar < 2)
                for (byte gotiNo = 0; gotiNo < 4; gotiNo++)
                    if (this.goti[currColTurn][gotiNo] == -6) return gotiNo;

        }
        return goti;
    }

    //endregion _____BOT FOR COMPUTER_______
    public interface LudoLisner {
        void onPlaySound(byte no);
    }
}
