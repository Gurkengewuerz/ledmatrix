package de.hochschule_bochum.matrixtable.snake.objects;

import de.hochschule_bochum.matrixtable.engine.BoardObject;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

/**
 * Created by nikla on 04.07.2017.
 */
public class SnakeBoard extends BoardObject<SnakePoint> {
    public SnakeBoard(Display display) {
        super(SnakePoint.class, display);
    }

    @Override
    public void clear() {
        display.clear();
    }


    public void draw(SnakeTile snake, SnakePoint cherry) {
        SnakeTile[][] drawingTiles = new SnakeTile[getLength()][getWidth()];
        snake.getSnakeParts().forEach(part -> {
            if (part.getX() <= 0 || part.getY() <= 0) return;
            if (part.getX() > getWidth() || part.getY() > getLength()) return;
            drawingTiles[part.getY() - 1][part.getX() - 1] = snake;
        });

        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                SnakeTile tile = drawingTiles[y][x];
                if (tile == null)
                    display.set(x + 1, y + 1, null, false);
                else
                    display.set(x + 1, y + 1, tile.getColor(), false);
            }
        }

        if (cherry != null) display.set(cherry.getX(), cherry.getY(), cherry.getColor(), false);

        display.update();
    }
}