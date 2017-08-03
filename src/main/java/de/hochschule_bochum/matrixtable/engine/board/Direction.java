package de.hochschule_bochum.matrixtable.engine.board;

/**
 * Created by nikla on 05.07.2017.
 */
public enum Direction {
    LEFT,
    RIGHT,
    UP,
    DOWN,
    TOP,
    BOTTOM;

    @Override
    public String toString() {
        switch (this) {
            case LEFT:
                return "left";
            case RIGHT:
                return "right";
            case UP:
                return "up";
            case DOWN:
                return "down";
            case TOP:
                return "top";
            case BOTTOM:
                return "bottom";
        }
        return "";
    }

    public Direction copy() {
        return Direction.values()[this.ordinal()];
    }
}
