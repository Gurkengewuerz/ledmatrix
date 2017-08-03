package de.hochschule_bochum.matrixtable.game.tetris.objects;

import java.awt.*;

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
                return new Tile(Color.CYAN,
                        new int[]{0, 0, 0, 0},
                        new int[]{1, 1, 1, 1},
                        new int[]{0, 0, 0, 0},
                        new int[]{0, 0, 0, 0}
                );
            case J:
                return new Tile(Color.BLUE,
                        new int[]{1, 0, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            case L:
                return new Tile(Color.ORANGE,
                        new int[]{0, 0, 1},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            case O:
                return new Tile(Color.YELLOW,
                        new int[]{1, 1},
                        new int[]{1, 1}
                );
            case S:
                return new Tile(Color.RED,
                        new int[]{0, 1, 1},
                        new int[]{1, 1, 0},
                        new int[]{0, 0, 0}
                );
            case Z:
                return new Tile(Color.GREEN,
                        new int[]{1, 1, 0},
                        new int[]{0, 1, 1},
                        new int[]{0, 0, 0}
                );
            case T:
                return new Tile(Color.MAGENTA,
                        new int[]{0, 1, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            default:
                throw new IllegalArgumentException("Unknown Tile");
        }
    }
}
