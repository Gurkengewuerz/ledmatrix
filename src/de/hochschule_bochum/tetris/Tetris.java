package de.hochschule_bochum.tetris;

import com.ivan.xinput.XInputButtonsDelta;
import com.ivan.xinput.XInputComponentsDelta;
import com.ivan.xinput.XInputDevice;
import com.ivan.xinput.enums.XInputButton;
import com.ivan.xinput.exceptions.XInputNotLoadedException;
import de.hochschule_bochum.tetris.objects.*;

import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tetris {

    private int currentX;
    private int currentY;
    private Board gameBoard;
    private Tile currentTile;
    private Clock masterClock;
    private float tickSpeed;
    private boolean gameover;

    public void start() {
        reset();
        XInputDevice device = null;

        if (XInputDevice.isAvailable()) {
            System.out.println("XInput 1.3 is available on this platform.");
            try {
                XInputDevice[] devices = XInputDevice.getAllDevices();
                device = XInputDevice.getDeviceFor(0);
            } catch (XInputNotLoadedException e) {
                Logger.getLogger(Tetris.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        while (true) {

            if (gameover) {
                gameBoard.clear();
            }

            if (device != null) {
                if (device.poll()) {
                    XInputComponentsDelta componentsDelta = device.getDelta();
                    XInputButtonsDelta buttonsDelta = componentsDelta.getButtons();
                    if (buttonsDelta.isPressed(XInputButton.DPAD_LEFT)) {
                        move(Direction.LEFT);
                    } else if (buttonsDelta.isPressed(XInputButton.DPAD_RIGHT)) {
                        move(Direction.RIGHT);
                    } else if (buttonsDelta.isPressed(XInputButton.DPAD_UP)) {
                        rotatePiece(Direction.RIGHT);
                    } else if (buttonsDelta.isPressed(XInputButton.DPAD_DOWN)) {
                        getMasterClock().setTickSpeed(35f);
                    } else if (buttonsDelta.isReleased(XInputButton.DPAD_DOWN)) {
                        getMasterClock().setTickSpeed(getTickSpeed());
                    } else if (buttonsDelta.isPressed(XInputButton.BACK)) {
                        reset();
                    } else if (buttonsDelta.isPressed(XInputButton.START)) {
                        if (getMasterClock().isPaused()) {
                            getMasterClock().start();
                        } else {
                            getMasterClock().pause();
                        }
                    }
                }
            }

            if (masterClock.timeElapsed()) {
                updateGame();
            }

            gameBoard.draw(currentTile, currentX, currentY);
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public void reset() {
        gameover = false;
        tickSpeed = 1f;
        masterClock = new Clock(tickSpeed, false);
        gameBoard = new Board();
        spawn();
    }

    public void updateGame() {
        if (gameBoard.isValidAndEmpty(currentTile, currentX, currentY + 1)) {
            currentY++;
        } else {
            gameBoard.setPieces(currentTile, currentX, currentY);
            int cleared = gameBoard.checkLines();
            if (cleared > 0) {
                // add Highscore
            }

            tickSpeed += 0.035f;
            masterClock.setTickSpeed(tickSpeed);
            spawn();
        }
    }

    public void spawn() {
        Random rnd = new Random();
        currentTile = TileType.values()[rnd.nextInt(TileType.values().length)].getShape();
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