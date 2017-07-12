package de.hochschule_bochum;

import de.hochschule_bochum.server.controller.ButtonState;
import de.hochschule_bochum.server.controller.ControllerServer;
import de.hochschule_bochum.tetris.Tetris;
import de.hochschule_bochum.tetris.objects.Direction;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.07.2017.
 */
public class TetrisLED {

    public static void main(String[] args) {
        // TODO: Check native libraries
        Logger.getLogger(TetrisLED.class.getName()).log(Level.INFO, "Tetris LED Started");

        Tetris tetris = new Tetris();

        ControllerServer controller = new ControllerServer();
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

        tetris.start();
    }
}
