import java.awt.*;

public class Food {
    private int foodX;
    private int foodY;
    private final int dotSize;
    private Settings colorTheme;

    public Food(int x, int y, int dotSize, Settings colorTheme) {
        foodX = x;
        foodY = y;
        this.dotSize = dotSize;
        this.colorTheme = colorTheme;
    }

    public int getFoodX() {
        return foodX;
    }

    public int getFoodY() {
        return foodY;
    }

    public void placeFood(int width, int height, int borderThickness) {
        int r = (int) (Math.random() * ((width - 2 * borderThickness) / dotSize));
        foodX = r * dotSize + borderThickness;

        int yR = (int) (Math.random() * ((height - 2 * borderThickness) / dotSize));
        foodY = yR * dotSize + borderThickness;
    }

    public void draw(Graphics g) {
        g.setColor(colorTheme.getFoodColor());
        g.fillRect(foodX, foodY, dotSize, dotSize);
    }
}