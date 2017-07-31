package de.hochschule_bochum.matrixtable.tetris.objects;

import de.hochschule_bochum.matrixtable.engine.BoardTile;
import de.hochschule_bochum.matrixtable.engine.Direction;
import de.hochschule_bochum.matrixtable.utils.ArrayUtils;
import de.hochschule_bochum.matrixtable.utils.Utils;

import java.awt.*;

/**
 * Created by nikla on 04.07.2017.
 */
public class Tile implements BoardTile {
    private int size;
    private int[][] shapes;
    private Color c;
    private int rotation;

    public Tile(Color c, int[]... block) {
        this.c = c;
        this.size = block.length;
        shapes = block;
        rotation = 0;
    }

    public Tile(Tile copyTile) {
        this.size = copyTile.getSize();
        this.shapes = copyTile.getShapes();
        this.c = copyTile.getColor();
        this.rotation = copyTile.getRotation();
    }

    public int getSize() {
        return size;
    }

    public int[][] getShapes() {
        return getRotatedTile(rotation);
    }

    public Color getColor() {
        return c;
    }

    public int getRotation() {
        return rotation;
    }

    private int[][] getRotatedTile(int rotation) {
        int[][] input = shapes;

        for (int i = 0; i < rotation; i++) {
            input = ArrayUtils.rotateArray(input);
        }

        return input;
    }

    public int roate(Direction direction) {
        if (direction == Direction.LEFT) {
            rotation--;
            if (rotation <= -1) rotation = 3;
            return rotation;
        } else if (direction == Direction.RIGHT) {
            rotation++;
            if (rotation >= 4) rotation = 0;
            return rotation;
        }
        return rotation;
    }

    public int rotateRandom() {
        rotation = Utils.randInt(0, 4);
        return rotation;
    }

    public boolean isPartOfTile(int x, int y) {
        return getRotatedTile(rotation)[y][x] == 1;
    }

    public int getSpaceLeft() {
        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                if (isPartOfTile(x, y)) {
                    return x;
                }
            }
        }
        return -1;
    }

    public int getSpaceRight() {
        for (int x = size - 1; x >= 0; x--) {
            for (int y = 0; y < size; y++) {
                if (isPartOfTile(x, y)) {
                    return size - x;
                }
            }
        }
        return -1;
    }

    public int getSpaceTop() {
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (isPartOfTile(x, y)) {
                    return y;
                }
            }
        }
        return -1;
    }

    public int getSpaceBottom() {
        for (int y = size - 1; y >= 0; y--) {
            for (int x = 0; x < size; x++) {
                if (isPartOfTile(x, y)) {
                    return size - y;
                }
            }
        }
        return -1;
    }
}
