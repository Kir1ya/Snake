import javax.swing.*;
import java.awt.*;

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
    private JPanel gamePanel;
    private JPanel homePanel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private Settings settings = new Settings(Theme.DEFAULT);
    private Difficulty difficulty = Difficulty.MEDIUM;

    public Main() {
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        createHomePanel();
        createGamePanel();

        mainPanel.add(homePanel, "home");
        mainPanel.add(gamePanel, "game");

        add(mainPanel);
        pack();
        setLocationRelativeTo(null);

        cardLayout.show(mainPanel, "home");
    }

    private void createHomePanel() {
        //Home Screen
        homePanel = new JPanel();
        homePanel.setPreferredSize(new Dimension(WIDTH, TOTAL_HEIGHT));
        homePanel.setLayout(new GridBagLayout());
        homePanel.setBackground(settings.getBG());

        Font font = new Font("Arial", Font.BOLD, 16);

        JButton startButton = new JButton("Start Game");
        startButton.setFont(font);
        startButton.addActionListener(e -> startGame());

        JButton difficultyButton = new JButton("Select Difficulty");
        difficultyButton.setFont(font);
        difficultyButton.addActionListener(e -> selectDifficulty());

        JButton themeButton = new JButton("Change Theme");
        themeButton.setFont(font);
        themeButton.addActionListener(e -> changeTheme());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        homePanel.add(startButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        homePanel.add(difficultyButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        homePanel.add(themeButton, gbc);
    }

    private void createGamePanel() {
        //Game Screen
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

        GameMovement.keyBinds(gamePanel, this);
    }

    private void startGame() {
        cardLayout.show(mainPanel, "game");
        restartGame();
    }

    private void selectDifficulty() {
        //Default is medium
        String[] options = {"Easy", "Medium", "Hard"};
        int choice = JOptionPane.showOptionDialog(this, "Select Difficulty", "Difficulty",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[1]);

        switch (choice) {
            case 0 -> difficulty = Difficulty.EASY;
            case 1 -> difficulty = Difficulty.MEDIUM;
            case 2 -> difficulty = Difficulty.HARD;
        }
    }

    private void changeTheme() {
        //Default is light mode
        String[] options = {"Default", "Monochrome", "Ocean", "Void"};
        int choice = JOptionPane.showOptionDialog(this, "Select Theme", "Theme",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> settings = new Settings(Theme.DEFAULT);
            case 1 -> settings = new Settings(Theme.MONOCHROME);
            case 2 -> settings = new Settings(Theme.OCEAN);
            case 3 -> settings = new Settings(Theme.VOID);
        }
        homePanel.setBackground(settings.getBG());
    }

    private void stopTimers() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
        if (secondTimer != null) {
            secondTimer.stop();
        }
    }

    public void restartGame() {
        stopTimers();
        bodyParts = 4;
        fruitsEaten = 0;
        direction = 'R';
        elapsedTime = 0;
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 5 * DOT_SIZE - i * DOT_SIZE + BORDER_THICKNESS;
            y[i] = 5 * DOT_SIZE + HEADER_HEIGHT + BORDER_THICKNESS;
        }
        placeFood();
        startTimers();
        running = true;
        gamePanel.repaint();
    }

    private void startTimers() {
        int gameSpeed = switch (difficulty) {
            case EASY -> 120;
            case MEDIUM -> 90;
            case HARD -> 60;
        };
        gameTimer = new Timer(gameSpeed, e -> gameUpdate());
        gameTimer.start();
        secondTimer = new Timer(1000, e -> {
            if (running) {
                elapsedTime++;
                gamePanel.repaint();
            }
        });
        secondTimer.start();
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
            case 'U':
                y[0] -= DOT_SIZE;
                break;
            case 'D':
                y[0] += DOT_SIZE;
                break;
            case 'L':
                x[0] -= DOT_SIZE;
                break;
            case 'R':
                x[0] += DOT_SIZE;
                break;
        }
    }

    private void checkFood() {
        if ((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            fruitsEaten++;
            Sound.playSound("/eat.wav");
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
            secondTimer.stop();
            Sound.playSound("/die.wav");
            showGameOver();
        }
    }

    private void showGameOver() {
        JOptionPane.showMessageDialog(this, "Game Over\nScore: " + fruitsEaten, "Game Over", JOptionPane.INFORMATION_MESSAGE);
        cardLayout.show(mainPanel, "home");
    }

    public boolean isRunning() {
        return running;
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
            g.setColor(settings.getScoreColor());
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
