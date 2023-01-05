package de.hochschule_bochum.matrixtable.server.controller;

/**
 * Created by nikla on 07.07.2017.
 */
public enum Key {
    UP,
    DOWN,
    LEFT,
    RIGHT,
    START,
    SELECT,
    A,
    B,
    X,
    Y;

    public static Key keyToString(String key) {
        switch (key) {
            case "up":
                return UP;

            case "down":
                return DOWN;

            case "left":
                return LEFT;

            case "right":
                return RIGHT;

            case "start":
                return START;

            case "select":
                return SELECT;

            case "a":
                return A;

            case "b":
                return B;

            case "x":
                return X;

            case "y":
                return Y;
        }
        return null;
    }
}
