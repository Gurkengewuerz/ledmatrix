package de.hochschule_bochum.matrixtable.engine;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.lang.reflect.Array;

/**
 * Created by nikla on 14.07.2017.
 */
public abstract class BoardObject<T extends BoardTile> {
    private final int ROW_X = 10; // X. COL
    private final int ROW_Y = 20; // Y, Rows
    protected T[][] tiles;
    protected Display display;

    @SuppressWarnings("unchecked")
    public BoardObject(Class<T> c, Display display) {
        this.display = display;
        this.tiles = (T[][]) Array.newInstance(c, ROW_Y, ROW_X);
    }

    public int getWidth() {
        return ROW_X;
    }

    public int getLength() {
        return ROW_Y;
    }

    public void clear() {
        for (int y = 0; y < ROW_Y; y++) {
            for (int x = 0; x < ROW_X; x++) {
                tiles[y][x] = null;
            }
        }
    }

    protected boolean isOccupied(int x, int y) {
        return tiles[y][x] != null;
    }

    public boolean isValidAndEmpty(int x, int y) {
        return isOccupied(x, y);
    }

    public T getTile(int x, int y) {
        if (x >= getWidth()) return null;
        if (y >= getLength()) return null;
        return tiles[y][x];
    }

    public void setTile(T tile, int x, int y) {
        tiles[y][x] = tile;
    }

    public void draw(int currentX, int currentY) {
        for (int y = 0; y < ROW_Y; y++) {
            for (int x = 0; x < ROW_X; x++) {
                T tile = tiles[y][x];
                if (tile == null)
                    display.set(x + 1, y + 1, null, false);
                else
                    display.set(x + 1, y + 1, tile.getColor(), false);
            }
        }
        display.update();
    }
}
