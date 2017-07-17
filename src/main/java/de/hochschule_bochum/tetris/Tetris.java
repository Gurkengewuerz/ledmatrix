package de.hochschule_bochum.tetris;

import de.hochschule_bochum.engine.Clock;
import de.hochschule_bochum.engine.Direction;
import de.hochschule_bochum.engine.Game;
import de.hochschule_bochum.engine.GameStatus;
import de.hochschule_bochum.ledmatrix.objects.Display;
import de.hochschule_bochum.tetris.objects.TetrisBoard;
import de.hochschule_bochum.tetris.objects.Tile;
import de.hochschule_bochum.tetris.objects.TileType;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tetris implements Game{

    private int currentX;
    private int currentY;
    private TetrisBoard gameBoard;
    private Tile currentTile;
    private Clock masterClock;
    private float tickSpeed;
    private boolean gameover;
    private Display display;
    private GameStatus status;

    public Tetris(Display display, GameStatus status) {
        this.status = status;
        this.display = display;
    }

    public void start() {
        reset();

        while (true) {

            if (gameover && masterClock.isPaused()) {
                status.setStatus(GameStatus.Status.GAMEOVER);
                gameBoard.clear();
            } else if (masterClock.isPaused()) {
                status.setStatus(GameStatus.Status.PAUSE);
            } else {
                status.setStatus(GameStatus.Status.RUNNING);
            }

            if (masterClock.timeElapsed()) {
                updateGame();
            }

            gameBoard.draw(currentTile, currentX, currentY);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }

        }
    }

    public void reset() {
        if (display == null) throw new NullPointerException("Display is not set.");
        gameover = false;
        tickSpeed = 1f;
        masterClock = new Clock(tickSpeed, false);
        gameBoard = new TetrisBoard(display);
        status.reset(GameStatus.Status.RUNNING, GameStatus.TimerType.NONE, 0);
        spawn();
    }

    public void updateGame() {
        if (gameBoard.isValidAndEmpty(currentTile, currentX, currentY + 1)) {
            currentY++;
        } else {
            gameBoard.setPieces(currentTile, currentX, currentY);
            int cleared = gameBoard.checkLines();
            if (cleared > 0) {
                switch (cleared) {
                    case 1:
                        status.addHighScore(40 * status.getLevel());
                        break;

                    case 2:
                        status.addHighScore(100 * status.getLevel());
                        break;

                    case 3:
                        status.addHighScore(300 * status.getLevel());
                        break;

                    case 4:
                        status.addHighScore(1200 * status.getLevel());
                        break;
                }
            }

            tickSpeed += 0.035f;
            masterClock.setTickSpeed(tickSpeed);
            spawn();
        }
    }

    private void spawn() {
        Random rnd = new Random();
        currentTile = TileType.values()[rnd.nextInt(TileType.values().length)].getShape();
        currentTile.rotateRandom();
        currentY = 0;
        currentX = 5 - (currentTile.getSize() >> 1);
        if (!gameBoard.isValidAndEmpty(currentTile, currentX, currentY + 1)) {
            gameover = true;
            masterClock.pause();
            gameBoard.clear();
            currentTile = null;
        }
    }

    public void move(Direction direction) {
        if (direction == Direction.LEFT) {
            if (masterClock.isPaused()) return;
            if (!gameBoard.isValidAndEmpty(currentTile, currentX - 1, currentY)) return;
            currentX--;
        } else if (direction == Direction.RIGHT) {
            if (masterClock.isPaused()) return;
            if (!gameBoard.isValidAndEmpty(currentTile, currentX + 1, currentY)) return;
            currentX++;
        }
    }

    public void rotatePiece(Direction direction) {
        if (currentTile == null) return;
        int newColumn = currentX;
        int newRow = currentY;


        Tile tileCopy = new Tile(currentTile);
        tileCopy.roate(direction);

        int left = tileCopy.getSpaceLeft();
        int right = tileCopy.getSpaceRight();
        int top = tileCopy.getSpaceTop();
        int bottom = tileCopy.getSpaceBottom();

        if (currentX < -left) {
            newColumn -= currentX - left;
        } else if (currentX + currentTile.getSize() - right >= gameBoard.getWidth()) {
            newColumn -= (currentX + currentTile.getSize() - right) - gameBoard.getWidth() + 1;
        }

        if (currentY < -top) {
            newRow -= currentY - top;
        } else if (currentY + currentTile.getSize() - bottom >= gameBoard.getLength()) {
            newRow -= (currentY + currentTile.getSize() - bottom) - gameBoard.getLength() + 1;
        }

        if (gameBoard.isValidAndEmpty(tileCopy, newColumn, newRow)) {
            currentTile.roate(direction);
            currentY = newRow;
            currentX = newColumn;
        }
    }

    public Clock getMasterClock() {
        return masterClock;
    }

    public float getTickSpeed() {
        return tickSpeed;
    }
}