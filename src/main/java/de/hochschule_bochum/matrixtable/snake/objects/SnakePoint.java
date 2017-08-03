package de.hochschule_bochum.matrixtable.snake.objects;

import de.hochschule_bochum.matrixtable.engine.board.BoardTile;

import java.awt.*;

/**
 * Created by nikla on 14.07.2017.
 */
public class SnakePoint implements BoardTile {
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

    public boolean equals(SnakePoint other) {
        return x == other.getX() && y == other.getY();
    }

    @Override
    public Color getColor() {
        return Color.CYAN;
    }

    @Override
    public String toString() {
        return "SnakePoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
