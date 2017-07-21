package de.hochschule_bochum.matrixtable.tetris.objects;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Color;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.ColorType;

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
                return new Tile(new Color(ColorType.CYAN),
                        new int[]{0, 0, 0, 0},
                        new int[]{1, 1, 1, 1},
                        new int[]{0, 0, 0, 0},
                        new int[]{0, 0, 0, 0}
                );
            case J:
                return new Tile(new Color(ColorType.BLUE),
                        new int[]{1, 0, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            case L:
                return new Tile(new Color(ColorType.ORANGE),
                        new int[]{0, 0, 1},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            case O:
                return new Tile(new Color(ColorType.YELLOW),
                        new int[]{1, 1},
                        new int[]{1, 1}
                );
            case S:
                return new Tile(new Color(ColorType.RED),
                        new int[]{0, 1, 1},
                        new int[]{1, 1, 0},
                        new int[]{0, 0, 0}
                );
            case Z:
                return new Tile(new Color(ColorType.GREEN),
                        new int[]{1, 1, 0},
                        new int[]{0, 1, 1},
                        new int[]{0, 0, 0}
                );
            case T:
                return new Tile(new Color(ColorType.PURPLE),
                        new int[]{0, 1, 0},
                        new int[]{1, 1, 1},
                        new int[]{0, 0, 0}
                );
            default:
                throw new IllegalArgumentException("Unknown Tile");
        }
    }
}
