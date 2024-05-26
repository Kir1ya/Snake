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
            case MONOCHROME:
                BG = Color.DARK_GRAY;
                WALL_COLOR = Color.BLACK;
                SNAKE_ACCENT_COLOR = new Color(130, 130, 130);
                SNAKE_SECONDARY_COLOR = new Color(99, 99, 99);
                FOOD_COLOR = new Color(232, 232, 232);
                SCORE_COLOR = Color.WHITE;
                GAME_OVER_COLOR = Color.RED;
                break;
            case OCEAN:
                BG = new Color(0, 53, 84);
                WALL_COLOR = new Color(5, 25, 35);
                SNAKE_ACCENT_COLOR =  new Color(0, 100, 148);
                SNAKE_SECONDARY_COLOR = new Color(5, 130, 202);
                FOOD_COLOR =  new Color(249, 82, 46);
                SCORE_COLOR = Color.WHITE;
                GAME_OVER_COLOR = Color.RED;
                break;
            case VOID:
                BG = Color.BLACK;
                WALL_COLOR = Color.WHITE;
                SNAKE_ACCENT_COLOR =  new Color(235, 110, 238);
                SNAKE_SECONDARY_COLOR = new Color(181, 78, 184);
                FOOD_COLOR =  new Color(124, 198, 197);
                SCORE_COLOR = Color.WHITE;
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