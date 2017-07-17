package de.hochschule_bochum.tetris;

import de.hochschule_bochum.LEDTable;
import de.hochschule_bochum.engine.Direction;
import de.hochschule_bochum.engine.GameStatus;
import de.hochschule_bochum.ledmatrix.objects.Display;
import de.hochschule_bochum.server.controller.ButtonState;
import de.hochschule_bochum.server.controller.ControllerServer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 14.07.2017.
 */
public class TetrisTest {
    public static void main(String[] args) {
        Logger.getLogger(LEDTable.class.getName()).log(Level.INFO, "Tetris LED Started");
        Display display = new Display(10, 20, true);
        GameStatus status = new GameStatus();

        Tetris tetris = new Tetris(display, status);

        ControllerServer controller = new ControllerServer(status);
        controller.setOnKeyChangeListener((key, newState) -> {
            switch (key) {
                case LEFT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    tetris.move(Direction.LEFT);
                    break;

                case RIGHT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    tetris.move(Direction.RIGHT);
                    break;

                case UP:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    tetris.rotatePiece(Direction.RIGHT);
                    break;

                case DOWN:
                    if (newState == ButtonState.BUTTON_UP) {
                        tetris.getMasterClock().setTickSpeed(tetris.getTickSpeed());
                    } else {
                        tetris.getMasterClock().setTickSpeed(35f);
                    }
                    break;

                case START:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    if (tetris.getMasterClock().isPaused()) {
                        tetris.getMasterClock().start();
                    } else {
                        tetris.getMasterClock().pause();
                    }
                    break;

                case SELECT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    tetris.reset();
                    break;
            }
        });

        controller.setOnKeyHoldListener(key -> {
            controller.setTimesPerSec(tetris.getTickSpeed());
            System.out.println("Hold " + key);
        });

        controller.setOnDisconnectListener(device -> {
            System.exit(1);
        });

        tetris.start();
    }
}
