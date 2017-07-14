package de.hochschule_bochum.tetris.objects;

import de.hochschule_bochum.ledmatrix.objects.Display;

/**
 * Created by nikla on 04.07.2017.
 */
public class Board {
    private int ROW_X = 10; // X. COL
    private int ROW_Y = 20; // Y, Rows
    private Tile[][] tiles;
    private Display display;

    public Board(Display display) {
        tiles = new Tile[ROW_Y][ROW_X];
        this.display = display;
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

    private boolean isOccupied(int x, int y) {
        return tiles[y][x] != null;
    }

    public boolean isValidAndEmpty(Tile tile, int x, int y) {
        if (x < -tile.getSpaceLeft() || x + tile.getSize() - tile.getSpaceRight() >= ROW_X) return false;
        if (y < -tile.getSpaceTop() || y + tile.getSize() - tile.getSpaceBottom() >= ROW_Y) return false;

        for (int col = 0; col < tile.getSize(); col++) {
            for (int row = 0; row < tile.getSize(); row++) {
                if (tile.isPartOfTile(col, row) && isOccupied(x + col, y + row)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Tile getTile(int x, int y) {
        return tiles[y][x];
    }

    private void setTile(Tile tile, int x, int y) {
        tiles[y][x] = tile;
    }

    public void setPieces(Tile tile, int x, int y) {
        for (int col = 0; col < tile.getSize(); col++) {
            for (int row = 0; row < tile.getSize(); row++) {
                if (tile.isPartOfTile(col, row)) {
                    setTile(tile, col + x, row + y);
                }
            }
        }
    }

    /**
     * Check if any Line is full.
     *
     * @return int Number of cleared Lines
     */
    public int checkLines() {
        int completedLines = 0;
        for (int row = 0; row < ROW_Y; row++) {
            if (checkLine(row)) {
                completedLines++;
            }
        }
        return completedLines;
    }

    private boolean checkLine(int line) {
        for (int col = 0; col < ROW_X; col++) {
            if (!isOccupied(col, line)) {
                return false;
            }
        }

        for (int row = line - 1; row >= 0; row--) {
            for (int col = 0; col < ROW_X; col++) {
                setTile(getTile(col, row), col, row + 1);
            }
        }
        return true;
    }

    public void draw(Tile currTile, int currentX, int currentY) {
        Tile[][] drawingTiles = new Tile[ROW_Y][ROW_X];
        for (int y = 0; y < ROW_Y; y++) {
            for (int x = 0; x < ROW_X; x++) {
                Tile tile = getTile(x, y);
                drawingTiles[y][x] = tile;
            }
        }


        if (currTile != null) {
            for (int y = 0; y < currTile.getSize(); y++) {
                for (int x = 0; x < currTile.getSize(); x++) {
                    if(currentY + y > ROW_Y ||currentX + x > ROW_X) return;
                    if (currTile.isPartOfTile(x, y)) drawingTiles[currentY + y][currentX + x] = currTile;
                }
            }
        }

        for (int y = 0; y < ROW_Y; y++) {
            for (int x = 0; x < ROW_X; x++) {
                Tile tile = drawingTiles[y][x];
                if (tile == null)
                    display.set(x + 1, y + 1, null, false);
                else
                    display.set(x + 1, y + 1, tile.getColor(), false);
            }
        }
        display.update();
    }
}
