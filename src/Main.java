import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Main extends JFrame {
    private final int WIDTH = 500, HEIGHT_WITHOUT_HEADER = 500;
    private final int HEADER_HEIGHT = 40;
    private final int TOTAL_HEIGHT = HEIGHT_WITHOUT_HEADER + HEADER_HEIGHT;
    private final int DOT_SIZE = 20;
    private final int BORDER_THICKNESS = DOT_SIZE;
    private int[] x = new int[(WIDTH / DOT_SIZE) * (WIDTH / DOT_SIZE)];
    private int[] y = new int[(WIDTH / DOT_SIZE) * (WIDTH / DOT_SIZE)];
    private int bodyParts = 4;
    private int foodX;
    private int foodY;
    private int fruitsEaten = 0;
    private char direction = 'R';
    private boolean running = false;
    private Timer gameTimer;
    private Timer secondTimer;
    private int elapsedTime = 0;
    private final JPanel gamePanel;
    private Settings settings = new Settings(Theme.DEFAULT);

    public Main() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JDialog dropdown = new JDialog();


        gamePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                setBackground(settings.getBG());

                doDrawing(g);
                showScore(g);
                showTime(g);
            }
        };
        gamePanel.setPreferredSize(new Dimension(WIDTH, TOTAL_HEIGHT));
        gamePanel.setFocusable(true);
        gamePanel.requestFocusInWindow();

        add(gamePanel);
        pack();
        setLocationRelativeTo(null);

        runGame();
        gameKeybinds();
    }

    private void runGame() {
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 5 * DOT_SIZE - i * DOT_SIZE + BORDER_THICKNESS;
            y[i] = 5* DOT_SIZE + HEADER_HEIGHT + BORDER_THICKNESS;
        }
        placeFood();
        gameTimer = new Timer(100, e -> gameUpdate());
        gameTimer.start();
        secondTimer = new Timer(1000, e -> {
            if (running) {
                elapsedTime++;
                gamePanel.repaint();
            }
        });
        secondTimer.start();
        running = true;
    }

    private void gameKeybinds() {
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "moveLeft");
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "moveLeft");
        gamePanel.getActionMap().put("moveLeft", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDirection('L');
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "moveRight");
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "moveRight");
        gamePanel.getActionMap().put("moveRight", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDirection('R');
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "moveUp");
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "moveUp");
        gamePanel.getActionMap().put("moveUp", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDirection('U');
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "moveDown");
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "moveDown");
        gamePanel.getActionMap().put("moveDown", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setDirection('D');
            }
        });

        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "restartGame");
        gamePanel.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "restartGame");
        gamePanel.getActionMap().put("restartGame", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!running) restartGame();
            }
        });
    }

    public char getDirection() {
        return direction;
    }

    public void setDirection(char newDirection) {
        if ((direction == 'R' && newDirection != 'L') ||
                (direction == 'L' && newDirection != 'R') ||
                (direction == 'U' && newDirection != 'D') ||
                (direction == 'D' && newDirection != 'U')) {
            this.direction = newDirection;
        }
    }

    private void placeFood() {
        int r = (int) (Math.random() * ((WIDTH - 2 * BORDER_THICKNESS) / DOT_SIZE));
        foodX = r * DOT_SIZE + BORDER_THICKNESS;

        int yR = (int) (Math.random() * ((HEIGHT_WITHOUT_HEADER - 2 * BORDER_THICKNESS) / DOT_SIZE));
        foodY = yR * DOT_SIZE + HEADER_HEIGHT + BORDER_THICKNESS;
    }

    private void gameUpdate() {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        gamePanel.repaint();
    }


    private void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U': y[0] -= DOT_SIZE; break;
            case 'D': y[0] += DOT_SIZE; break;
            case 'L': x[0] -= DOT_SIZE; break;
            case 'R': x[0] += DOT_SIZE; break;
        }
    }

    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            fruitsEaten++;
            placeFood();
        }
    }

    private void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((i > 4) && (x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
                break;
            }
        }
        if (y[0] < HEADER_HEIGHT + BORDER_THICKNESS || y[0] >= HEIGHT_WITHOUT_HEADER + HEADER_HEIGHT - BORDER_THICKNESS ||
                x[0] < BORDER_THICKNESS || x[0] >= WIDTH - BORDER_THICKNESS) {
            running = false;
        }
        if (!running) {
            gameTimer.stop();
        }
    }
    public boolean isRunning() {
        return running;
    }

    public void restartGame() {
        bodyParts = 6;
        fruitsEaten = 0;
        direction = 'R';
        elapsedTime = 0; // Reset the elapsed time
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 5 * DOT_SIZE - i * DOT_SIZE;
            y[i] = 5 * DOT_SIZE;
        }
        placeFood();
        gameTimer.start();
        running = true;
    }

    private void doDrawing(Graphics g) {
        g.setColor(settings.getBG());
        g.fillRect(0, 0, WIDTH, HEIGHT_WITHOUT_HEADER);
        g.setColor(settings.getWallColor());
        g.fillRect(0, HEADER_HEIGHT, WIDTH, BORDER_THICKNESS);
        g.fillRect(0, HEADER_HEIGHT, BORDER_THICKNESS, HEIGHT_WITHOUT_HEADER);
        g.fillRect(WIDTH - BORDER_THICKNESS, HEADER_HEIGHT, BORDER_THICKNESS, HEIGHT_WITHOUT_HEADER);
        g.fillRect(0, TOTAL_HEIGHT - BORDER_THICKNESS, WIDTH, BORDER_THICKNESS);
        if (running) {
            g.setColor(settings.getFoodColor());
            g.fillRect(foodX, foodY, DOT_SIZE, DOT_SIZE);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(settings.getSnakeAccentColor());
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                } else {
                    g.setColor(settings.getSnakeSecondaryColor());
                    g.fillRect(x[i], y[i], DOT_SIZE, DOT_SIZE);
                }
            }
        } else {
            gameOver(g);
        }
    }
    private void showTime(Graphics g) {
        if (running) {
            g.setColor(Color.BLACK);
            g.drawString("Time: " + elapsedTime + "s", 10, 20);
        }
    }
    private void showScore(Graphics g) {
        if (running) {
            g.setColor(settings.getScoreColor());
            g.drawString("Score: " + fruitsEaten, WIDTH - 120, 20);
        }
    }
    private void gameOver(Graphics g) {
        g.setColor(settings.getGameOverColor());
        g.drawString("Game Over", WIDTH / 2 - 50, HEIGHT_WITHOUT_HEADER / 2);
        g.drawString("Score: " + fruitsEaten, WIDTH / 2 - 50, HEIGHT_WITHOUT_HEADER / 2 + 20);
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            JFrame frame = new Main();
            frame.setVisible(true);
        });
    }
}
