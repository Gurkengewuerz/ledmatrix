package de.hochschule_bochum.matrixtable.game.arkanoid.objects;

import de.hochschule_bochum.matrixtable.engine.board.BoardTile;

import java.awt.*;

/**
 * Created by nikla on 31.07.2017.
 */
public class Ball implements BoardTile {

    private int x;
    private int y;
    private int incrementX;
    private int incrementY;

    public Ball(int x, int y) {
        this.x = x;
        this.y = y;
        incrementY = 1;
        incrementX = 1;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getIncrementX() {
        return incrementX;
    }

    public void setIncrementX(int incrementX) {
        this.incrementX = incrementX;
    }

    public int getIncrementY() {
        return incrementY;
    }

    public void setIncrementY(int incrementY) {
        this.incrementY = incrementY;
    }

    public void move() {
        setX(x + incrementX);
        setY(y + incrementY);
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
