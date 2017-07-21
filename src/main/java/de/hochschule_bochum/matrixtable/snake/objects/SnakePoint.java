package de.hochschule_bochum.matrixtable.snake.objects;

import de.hochschule_bochum.matrixtable.engine.BoardTile;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Color;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.ColorType;

/**
 * Created by nikla on 14.07.2017.
 */
public class SnakePoint implements BoardTile{
    private int x;
    private int y;

    public SnakePoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean equals(SnakePoint other){
        return x == other.getX() && y == other.getY();
    }

    @Override
    public Color getColor() {
        return new Color(ColorType.CYAN);
    }

    @Override
    public String toString() {
        return "SnakePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
