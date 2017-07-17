package de.hochschule_bochum.snake.objects;

import de.hochschule_bochum.engine.BoardTile;
import de.hochschule_bochum.ledmatrix.objects.Color;
import de.hochschule_bochum.ledmatrix.objects.ColorType;

import java.util.ArrayList;

/**
 * Created by nikla on 14.07.2017.
 */
public class SnakeTile implements BoardTile {

    private ArrayList<SnakePoint> snakeParts = new ArrayList<>();
    private SnakePoint head;
    private int length;

    public SnakeTile() {
        this.length = 2;
        head = new SnakePoint(5, 8);
        snakeParts.clear();
        snakeParts.add(new SnakePoint(5, 7));
    }

    @Override
    public Color getColor() {
        return new Color(ColorType.GREEN);
    }

    public ArrayList<SnakePoint> getSnakeParts() {
        return snakeParts;
    }

    public boolean isValid(int x, int y) {
        for (SnakePoint snakePart : snakeParts) {
            if (snakePart.getX() == x && snakePart.getY() == y) return false;
        }
        return true;
    }

    public SnakePoint getHead() {
        return head;
    }

    public void setHead(SnakePoint head) {
        this.head = head;
    }

    public int getLength() {
        return length;
    }

    public void removeLast() {
        snakeParts.remove(0);
    }

    public void move() {
        snakeParts.add(new SnakePoint(head.getX(), head.getY()));
    }

    public void setLength(int length) {
        this.length = length;
    }

}
