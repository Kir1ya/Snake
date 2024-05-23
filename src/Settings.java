import java.awt.*;

public class Settings {
    private Color BG, WALL_COLOR, SNAKE_ACCENT_COLOR, SNAKE_SECONDARY_COLOR, FOOD_COLOR, SCORE_COLOR, GAME_OVER_COLOR;

    public Settings(Theme theme) {
        switch (theme) {
            case DEFAULT:
                BG = Color.WHITE;
                WALL_COLOR = Color.BLACK;
                SNAKE_ACCENT_COLOR = Color.GREEN;
                SNAKE_SECONDARY_COLOR = new Color(68, 179, 66);
                FOOD_COLOR = Color.RED;
                SCORE_COLOR = Color.BLACK;
                GAME_OVER_COLOR = Color.BLACK;
                break;
            case DARK:
                BG = Color.DARK_GRAY;
                WALL_COLOR = Color.BLACK;
                SNAKE_ACCENT_COLOR = new Color(119, 166, 191);
                SNAKE_SECONDARY_COLOR = new Color(90, 121, 138);
                FOOD_COLOR = new Color(237, 174, 85);
                SCORE_COLOR = Color.WHITE;
                GAME_OVER_COLOR = Color.RED;
                break;
            case HIGH_CONTRAST:
                BG = Color.WHITE;
                WALL_COLOR = Color.BLACK;
                SNAKE_ACCENT_COLOR = Color.ORANGE;
                SNAKE_SECONDARY_COLOR = Color.RED;
                FOOD_COLOR = Color.GREEN;
                SCORE_COLOR = Color.YELLOW;
                GAME_OVER_COLOR = Color.RED;
                break;
        }
    }

    public Color getBG() {
        return BG;
    }

    public Color getWallColor() {
        return WALL_COLOR;
    }

    public Color getSnakeAccentColor() {
        return SNAKE_ACCENT_COLOR;
    }

    public Color getSnakeSecondaryColor() {
        return SNAKE_SECONDARY_COLOR;
    }

    public Color getFoodColor() {
        return FOOD_COLOR;
    }

    public Color getScoreColor() {
        return SCORE_COLOR;
    }

    public Color getGameOverColor() {
        return GAME_OVER_COLOR;
    }
}