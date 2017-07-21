package de.hochschule_bochum.matrixtable.tetris.objects;

import de.hochschule_bochum.matrixtable.engine.BoardObject;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

/**
 * Created by nikla on 04.07.2017.
 */
public class TetrisBoard extends BoardObject<Tile> {
    public TetrisBoard(Display display) {
        super(Tile.class, display);
    }

    public boolean isValidAndEmpty(Tile tile, int x, int y) {
        if (x < -tile.getSpaceLeft() || x + tile.getSize() - tile.getSpaceRight() >= getWidth()) return false;
        if (y < -tile.getSpaceTop() || y + tile.getSize() - tile.getSpaceBottom() >= getLength()) return false;

        for (int col = 0; col < tile.getSize(); col++) {
            for (int row = 0; row < tile.getSize(); row++) {
                if (tile.isPartOfTile(col, row) && isOccupied(x + col, y + row)) {
                    return false;
                }
            }
        }
        return true;
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
        for (int row = 0; row < getLength(); row++) {
            if (checkLine(row)) {
                completedLines++;
            }
        }
        return completedLines;
    }

    private boolean checkLine(int line) {
        for (int col = 0; col < getWidth(); col++) {
            if (!isOccupied(col, line)) {
                return false;
            }
        }

        for (int row = line - 1; row >= 0; row--) {
            for (int col = 0; col < getWidth(); col++) {
                setTile(getTile(col, row), col, row + 1);
            }
        }
        return true;
    }

    public void draw(Tile currTile, int currentX, int currentY) {
        Tile[][] drawingTiles = new Tile[getLength()][getWidth()];
        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                Tile tile = getTile(x, y);
                drawingTiles[y][x] = tile;
            }
        }


        if (currTile != null) {
            for (int y = 0; y < currTile.getSize(); y++) {
                for (int x = 0; x < currTile.getSize(); x++) {
                    if (currentY + y > getLength() || currentX + x > getWidth()) return;
                    if (currTile.isPartOfTile(x, y)) drawingTiles[currentY + y][currentX + x] = currTile;
                }
            }
        }

        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
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
