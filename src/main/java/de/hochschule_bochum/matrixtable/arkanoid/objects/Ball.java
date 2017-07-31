package de.hochschule_bochum.matrixtable.arkanoid.objects;

import de.hochschule_bochum.matrixtable.engine.BoardTile;

import java.awt.*;

/**
 * Created by nikla on 31.07.2017.
 */
public class Ball implements BoardTile {

    private int x;
    private int y;
    private boolean moveY;
    private boolean moveX;
    private int maxX;
    private int maxY;

    public Ball(int x, int y, int maxX, int maxY) {
        this.x = x;
        this.y = y;
        this.maxX = maxX;
        this.maxY = maxY;
        moveX = true;
        moveY = true;
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

    public boolean isMoveY() {
        return moveY;
    }

    public void setMoveY(boolean moveY) {
        this.moveY = moveY;
    }

    public boolean isMoveX() {
        return moveX;
    }

    public void setMoveX(boolean moveX) {
        this.moveX = moveX;
    }

    public int getNextX() {
        int newX = 0;
        if (!moveY && !moveX) {
            newX = getX() - 1;
        } else if (!moveY && moveX) {
            newX = getX() + 1;
        } else if (moveY && !moveX) {
            newX = getX() - 1;
        } else if (moveY && moveX) {
            newX = getX() + 1;
        }
        if (newX < 0) newX = 0;
        if (newX > maxX) newX = maxX;
        return newX;
    }

    public int getNextY() {
        int newY = 0;
        if (!moveY && !moveX) {
            newY = getY() - 1;
        } else if (!moveY && moveX) {
            newY = getY() - 1;
        } else if (moveY && !moveX) {
            newY = getY() + 1;
        } else if (moveY && moveX) {
            newY = getY() + 1;
        }
        if (newY < 0) newY = 0;
        if (newY > maxY) newY = maxY;
        return newY;
    }

    public void move() {
        setX(getNextX());
        setY(getNextY());
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
