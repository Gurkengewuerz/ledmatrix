package de.hochschule_bochum.matrixtable;

import de.hochschule_bochum.matrixtable.arkanoid.Arkanoid;
import de.hochschule_bochum.matrixtable.engine.Config;
import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.Game;
import de.hochschule_bochum.matrixtable.engine.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.APA102Impl;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.WS2812Impl;
import de.hochschule_bochum.matrixtable.server.controller.ControllerServer;
import de.hochschule_bochum.matrixtable.snake.Snake;
import de.hochschule_bochum.matrixtable.tetris.Tetris;
import de.hochschule_bochum.matrixtable.webapi.NanoServer;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.07.2017.
 */
public class LEDTable {

    private static Config conf;

    public static void main(String[] args) throws IOException, InterruptedException {
        String settingsFile = "." + File.separator + "settings.json";
        if (args.length >= 1) {
            settingsFile = args[0];
        }
        File f = new File(settingsFile);

        conf = new Config(f);
        conf.load();

        if (conf.debug())
            Logger.getLogger(LEDTable.class.getName()).log(Level.INFO, "Using Settingsfile " + f.getAbsolutePath());

        if (conf.isFirstRun()) System.exit(0);

        Database.db = new Database(conf.getString("database"));

        // TODO: Check native libraries
        // TODO: Menu OOP
        Thread webserver = new Thread(() -> {
            try {
                new NanoServer(conf.getInt("api_port"));
            } catch (IOException e) {
                Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        webserver.start();

        Display display;

        switch (conf.getString("led_type").toLowerCase()) {
            case "ws2812":
                display = new WS2812Impl(10, 20, conf.debug());
                break;

            case "apa102":
            default:
                display = new APA102Impl(10, 20, conf.debug());
                break;
        }

        GameStatus status = new GameStatus();

        status.setApiURL(conf.getString("api_address"));

        status.addGame(new Tetris());
        status.addGame(new Snake());
        status.addGame(new Arkanoid());

        final Game[] currentGame = {null};
        ControllerServer controller = new ControllerServer(status);
        final boolean[] connected = {true};

        while (connected[0]) {
            currentGame[0] = null;

            controller.setOnGameSelected(game -> {
                if (currentGame[0] != null) currentGame[0].stop();
                currentGame[0] = game;
            });

            while (currentGame[0] == null && connected[0]) {
                Thread.sleep(50);
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
