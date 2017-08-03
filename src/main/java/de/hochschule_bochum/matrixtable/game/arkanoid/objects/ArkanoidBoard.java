package de.hochschule_bochum.matrixtable.game.arkanoid.objects;

import de.hochschule_bochum.matrixtable.engine.board.BoardObject;
import de.hochschule_bochum.matrixtable.engine.board.BoardTile;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

/**
 * Created by nikla on 31.07.2017.
 */
public class ArkanoidBoard extends BoardObject<BoardTile> {

    public ArkanoidBoard(Display display) {
        super(BoardTile.class, display);
    }

    public void draw(Ball ball, Paddle paddle) {
        BoardTile[][] drawingTiles = new BoardTile[getLength()][getWidth()];
        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                BoardTile tile = getTile(x, y);
                drawingTiles[y][x] = tile;
            }
        }


        if (ball != null) drawingTiles[ball.getY()][ball.getX()] = ball;
        if (paddle != null) {
            for (int i = 0; i < paddle.getSize(); i++) {
                drawingTiles[paddle.getY()][paddle.getX() + i] = paddle;
            }
        }


        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                BoardTile tile = drawingTiles[y][x];
                if (tile == null)
                    display.set(x + 1, y + 1, null, false);
                else
                    display.set(x + 1, y + 1, tile.getColor(), false);
            }
        }
        display.update();
    }
}
