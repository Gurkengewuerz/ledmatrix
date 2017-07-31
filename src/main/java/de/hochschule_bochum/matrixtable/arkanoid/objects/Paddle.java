package de.hochschule_bochum.matrixtable.arkanoid.objects;

import de.hochschule_bochum.matrixtable.engine.BoardTile;

import java.awt.*;

/**
 * Created by nikla on 31.07.2017.
 */
public class Paddle implements BoardTile {

    private int x;
    private int y;
    private int size;

    public Paddle(int x, int y, int size) {
        this.x = x;
        this.y = y;
        this.size = size;
    }

    public void moveLeft() {
        x--;
    }

    public void moveRight() {
        x++;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSize() {
        return size;
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
