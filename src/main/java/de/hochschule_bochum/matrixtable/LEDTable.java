package de.hochschule_bochum.matrixtable;

import de.hochschule_bochum.matrixtable.engine.Game;
import de.hochschule_bochum.matrixtable.engine.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.server.controller.ControllerServer;
import de.hochschule_bochum.matrixtable.snake.Snake;
import de.hochschule_bochum.matrixtable.tetris.Tetris;
import de.hochschule_bochum.matrixtable.webapi.NanoServer;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.07.2017.
 */
public class LEDTable {

    public static void main(String[] args) {
        // TODO: Check native libraries
        // TODO: Menu OOP
        Thread webserver = new Thread(() -> {
            try {
                new NanoServer(8081);
            } catch (IOException e) {
                Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        webserver.start();

        Display display = new Display(10, 20, true);
        GameStatus status = new GameStatus();

        status.addGame(new Tetris());
        status.addGame(new Snake());

        final Game[] currentGame = {null};
        ControllerServer controller = new ControllerServer(status);
        final boolean[] connected = {true};

        while (connected[0]) {
            currentGame[0] = null;

            controller.setOnGameSelected(game -> {
                if (currentGame[0] != null) currentGame[0].stop();
                currentGame[0] = game;
            });


            // TODO: Change with await
            while (currentGame[0] == null && connected[0]) {
                // Do nothing
            }

            if (currentGame[0] == null) continue;
            currentGame[0].setDisplay(display);
            currentGame[0].setStatus(status);

            controller.setOnKeyChangeListener((key, newState) -> {
                if (currentGame[0] == null) return;
                currentGame[0].sendKey(key, newState);
            });
            controller.setOnKeyHoldListener(key -> {
                if (currentGame[0] == null) return;
                currentGame[0].sendHold(key);
            });
            controller.setOnDisconnectListener(device -> {
                connected[0] = false;
                if (currentGame[0] == null) return;
                currentGame[0].stop();
                currentGame[0] = null;
            });

            currentGame[0].start();
            display.clear();
        }
        System.exit(1);
    }
}
