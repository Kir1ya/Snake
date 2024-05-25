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
        homePanel = new JPanel();
        homePanel.setPreferredSize(new Dimension(WIDTH, TOTAL_HEIGHT));
        homePanel.setLayout(new GridBagLayout());
        homePanel.setBackground(settings.getBG());

        Font font = new Font("Arial", Font.BOLD, 20);

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

        gameKeybinds();
    }

    private void startGame() {
        cardLayout.show(mainPanel, "game");
        restartGame();
    }

    private void selectDifficulty() {
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
        String[] options = {"Default", "Dark", "High Contrast"};
        int choice = JOptionPane.showOptionDialog(this, "Select Theme", "Theme",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);

        switch (choice) {
            case 0 -> settings = new Settings(Theme.DEFAULT);
            case 1 -> settings = new Settings(Theme.DARK);
            case 2 -> settings = new Settings(Theme.HIGH_CONTRAST);
        }
        // Update the home panel background to reflect the new theme
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

    private void resetSnake() {
        bodyParts = 4; // Reset snake length
        for (int i = 0; i < bodyParts; i++) {
            x[i] = (WIDTH / 2) - i * DOT_SIZE; // Start in the middle horizontally
            y[i] = (HEIGHT_WITHOUT_HEADER / 2) + HEADER_HEIGHT; // Start in the middle vertically
        }
    }

    private void resetTimer() {
        elapsedTime = 0;
    }

    public void restartGame() {
        stopTimers();
        bodyParts = 4;
        fruitsEaten = 0;
        direction = 'R';
        elapsedTime = 0; // Reset the elapsed time
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
            case EASY -> 200;
            case MEDIUM -> 100;
            case HARD -> 50;
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
                if (!running) {
                    cardLayout.show(mainPanel, "home");
                }
            }
        });
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
