package de.hochschule_bochum;

import de.hochschule_bochum.engine.Game;
import de.hochschule_bochum.engine.GameStatus;
import de.hochschule_bochum.ledmatrix.objects.Display;
import de.hochschule_bochum.server.controller.ControllerServer;
import de.hochschule_bochum.snake.Snake;
import de.hochschule_bochum.tetris.Tetris;

/**
 * Created by nikla on 04.07.2017.
 */
public class LEDTable {

    public static void main(String[] args) {
        // TODO: Check native libraries
        // TODO: Menu OOP
        Display display = new Display(10, 20, true);
        GameStatus status = new GameStatus();

        status.addGame(new Tetris());
        status.addGame(new Snake());

        final Game[] currentGame = {null};
        ControllerServer controller = new ControllerServer(status);
        final boolean[] connected = {true};

        while (connected[0]) {
            currentGame[0] = null;

            controller.setOnGameSelected(game -> currentGame[0] = game);

            // TODO: Change with await
            while (currentGame[0] == null) {
                // Do nothing
            }

            currentGame[0].setDisplay(display);
            currentGame[0].setStatus(status);

            controller.setOnGameSelected(null);
            controller.setOnKeyChangeListener((key, newState) -> {
                if(currentGame[0] == null) return;
                currentGame[0].sendKey(key, newState);
            });
            controller.setOnKeyHoldListener(key -> {
                if(currentGame[0] == null) return;
                currentGame[0].sendHold(key);
            });
            controller.setOnDisconnectListener(device -> {
                connected[0] = false;
                if(currentGame[0] == null) return;
                currentGame[0].stop();
                currentGame[0] = null;
            });

            currentGame[0].start();
        }
        System.exit(1);
    }
}
