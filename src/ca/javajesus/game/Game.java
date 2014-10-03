package ca.javajesus.game;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import ca.javajesus.game.entities.Player;
import ca.javajesus.game.gfx.Colours;
import ca.javajesus.game.gfx.Screen;
import ca.javajesus.game.gfx.SpriteSheet;
import ca.javajesus.level.Level;
import ca.javajesus.level.RandomLevel;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;

    public static final int WIDTH = 300;
    public static final int HEIGHT = WIDTH / 12 * 9;
    public static final int SCALE = 3;
    public static final String NAME = "Java Jesus by the Coders of Anarchy";

    public boolean running = false;

    private JFrame frame;
    public int tickCount = 0;

    private BufferedImage image = new BufferedImage(WIDTH, HEIGHT,
            BufferedImage.TYPE_INT_RGB);
    private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer())
            .getData();
    private int[] colours = new int[6 * 6 * 6];

    private Screen screen;
    public InputHandler input;
    public Level level;
    public Player player;
    JPanel gameFrame;
    private final String LEVEL_DISPLAY = "level";
    private final String LOADING_DISPLAY = "loading";

    /** This starts the game */
    public Game() {
        setMinimumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setMaximumSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));

        frame = new JFrame(NAME);

        // Eventually set the labels to an image of our logo
        JLabel label = new JLabel("CODERS OF ANARCHY");
        label.setFont(new Font("Verdana", 0, 50));
        JLabel label2 = new JLabel("Loading...");
        label2.setFont(new Font("Verdana", 0, 25));

        JPanel mainGame = new JPanel();
        mainGame.add(this, BorderLayout.CENTER);
        JPanel loadingScreen = new JPanel();
        loadingScreen.add(label, BorderLayout.CENTER);
        loadingScreen.add(label2, BorderLayout.CENTER);

        gameFrame = new JPanel(new CardLayout());

        gameFrame.add(mainGame, LEVEL_DISPLAY);
        gameFrame.add(loadingScreen, LOADING_DISPLAY);
        CardLayout cl = (CardLayout) (gameFrame.getLayout());
        cl.show(gameFrame, LOADING_DISPLAY);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        frame.getContentPane().add(gameFrame);
        frame.pack();

        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.requestFocus();
    }

    public void init() {
        int index = 0;
        for (int r = 0; r < 6; r++) {
            for (int g = 0; g < 6; g++) {
                for (int b = 0; b < 6; b++) {
                    int rr = (r * 255 / 5);
                    int gg = (g * 255 / 5);
                    int bb = (b * 255 / 5);

                    colours[index++] = rr << 16 | gg << 8 | bb;
                }
            }
        }

        screen = new Screen(WIDTH, HEIGHT, new SpriteSheet("/sprite_sheet.png"));
        input = new InputHandler(this);
        // level = new Level("/levels/water_test_level.png");
        level = new RandomLevel(WIDTH, HEIGHT);
        player = new Player(level, 0, 0, input);
        level.addEntity(player);
    }

    public synchronized void start() {
        running = true;
        new Thread(this).start();

    }

    public synchronized void stop() {
        running = false;
    }

    public void run() {

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }
        CardLayout cl = (CardLayout) (gameFrame.getLayout());
        cl.show(gameFrame, LEVEL_DISPLAY);

        long lastTime = System.nanoTime();
        double nsPerTick = 1000000000D / 60D;

        int ticks = 0;
        int frames = 0;

        long lastTimer = System.currentTimeMillis();
        double delta = 0;

        init();

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerTick;
            lastTime = now;
            boolean shouldRender = true;

            while (delta >= 1) {
                ticks++;
                tick();
                delta -= 1;
                shouldRender = true;
            }
            try {
                Thread.sleep(2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (shouldRender) {
                frames++;
                render();
            }

            if (System.currentTimeMillis() - lastTimer >= 1000) {
                lastTimer += 1000;
                System.out.println(ticks + " ticks," + frames + " frames");
                frames = 0;
                ticks = 0;
            }
        }

    }

    public void tick() {
        tickCount++;
        level.tick();
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
            return;
        }

        int xOffset = player.x - (screen.width / 2);
        int yOffset = player.y - (screen.height / 2);
        level.renderTile(screen, xOffset, yOffset);

        for (int x = 0; x < level.width; x++) {
            int color = Colours.get(-1, -1, -1, 000);
            if (x % 10 == 0 && x != 0) {
                color = Colours.get(-1, -1, -1, 500);
            }
        }

        level.renderEntities(screen);

        for (int y = 0; y < screen.height; y++) {
            for (int x = 0; x < screen.width; x++) {
                int colourCode = screen.pixels[x + y * screen.width];
                if (colourCode < 255)
                    pixels[x + y * WIDTH] = colours[colourCode];
            }

        }
        Graphics g = bs.getDrawGraphics();
        g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
        g.dispose();
        bs.show();
    }

    // Main Method Creation
    public static void main(String[] args) {
        new Game().start();

    }

}