package de.hochschule_bochum.tetris.objects;

import de.hochschule_bochum.ledmatrix.objects.Color;
import de.hochschule_bochum.tetris.utils.ArrayUtils;

/**
 * Created by nikla on 04.07.2017.
 */
public class Tile {
    private int size;
    private int[][] shapes;
    private Color c;
    private int rotation;

    public Tile(int[]... block) {
        this.size = block.length;
        shapes = new int[size][size];
        for (int i = 0; i < block.length; i++) {
            for (int j = 0; j < block[i].length; j++) {
                shapes[i][j] = block[i][j];
            }
        }
        rotation = 0;
    }

    public Tile(Tile copyTile) {
        this.size = copyTile.getSize();
        this.shapes = copyTile.getShapes();
        this.c = copyTile.getColor();
        this.rotation = copyTile.getRotation();
    }

    public Tile setColor(Color c) {
        this.c = c;
        return this;
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

    public boolean isPartOfTile(int x, int y) {
        return getRotatedTile(rotation)[x][y] == 1;
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
