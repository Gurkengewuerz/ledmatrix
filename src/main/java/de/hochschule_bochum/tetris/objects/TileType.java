package de.hochschule_bochum.tetris.objects;

import de.hochschule_bochum.ledmatrix.objects.Color;
import de.hochschule_bochum.ledmatrix.objects.ColorType;

/**
 * Created by nikla on 04.07.2017.
 */
public enum TileType {

    O,
    I,
    S,
    Z,
    J,
    L,
    T;

    public Tile getShape() {
        switch (this) {
            case I:
                return new Tile(
                        new int[]{0, 0, 0, 0},
                        new int[]{1, 1, 1, 1},
                        new int[]{0, 0, 0, 0},
                        new int[]{0, 0, 0, 0}
                ).setColor(new Color(ColorType.CYAN));
            case J:
                return new Tile(
                        new int[]{1, 0, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                ).setColor(new Color(ColorType.BLUE));
            case L:
                return new Tile(
                        new int[]{0, 0, 1},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                ).setColor(new Color(ColorType.ORANGE));
            case O:
                return new Tile(
                        new int[]{1, 1},
                        new int[]{1, 1}
                ).setColor(new Color(ColorType.YELLOW));
            case S:
                return new Tile(
                        new int[]{0, 1, 1},
                        new int[]{1, 1, 0},
                        new int[]{0, 0, 0}
                ).setColor(new Color(ColorType.RED));
            case Z:
                return new Tile(
                        new int[]{1, 1, 0},
                        new int[]{0, 1, 1},
                        new int[]{0, 0, 0}
                ).setColor(new Color(ColorType.GREEN));
            case T:
                return new Tile(
                        new int[]{0, 1, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                ).setColor(new Color(ColorType.PURPLE));
            default:
                throw new IllegalArgumentException("Unknown Tile");
        }
    }
}
