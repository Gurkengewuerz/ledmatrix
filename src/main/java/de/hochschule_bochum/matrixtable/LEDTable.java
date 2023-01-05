package de.hochschule_bochum.matrixtable;

import de.hochschule_bochum.matrixtable.engine.Config;
import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.game.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.APA102Impl;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.WS2812Impl;
import de.hochschule_bochum.matrixtable.server.controller.ControllerServer;
import de.hochschule_bochum.matrixtable.server.mqtt.MqttClient;
import de.hochschule_bochum.matrixtable.server.webapi.NanoServer;
import de.hochschule_bochum.matrixtable.server.webapi.NanoWebsocketServer;
import org.eclipse.paho.client.mqttv3.MqttException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.07.2017.
 */
public class LEDTable {

    private static Config conf;
    public static Display display;
    public static NanoWebsocketServer websocketServer;

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

        if (!(conf.getDouble("max_brightness") > 0D && conf.getDouble("max_brightness") <= 1D)) {
            Logger.getLogger(LEDTable.class.getName()).log(Level.SEVERE, "max_brightness Entry is invalid!");
            System.exit(0);
        }

        Database.db = new Database(conf.getString("database"));

        switch (conf.getString("led_type").toLowerCase()) {
            case "ws2812":
                display = new WS2812Impl(10, 20, conf.debug());
                break;

            case "apa102":
            default:
                display = new APA102Impl(10, 20, conf.debug());
                break;
        }

        display.setMaxBrightness(conf.getDouble("max_brightness"));

        GameStatus status = new GameStatus();
        status.setGame(null);
        status.setApiURL(conf.getString("api_address"));

        // TODO: Check native libraries
        // TODO: Menu OOP
        Thread webserver = new Thread(() -> {
            try {
                new NanoServer(conf.getInt("api_port"), status, display);
            } catch (IOException e) {
                Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        webserver.start();

        Thread websocket = new Thread(() -> {
            try {
                websocketServer = new NanoWebsocketServer(conf.getInt("ws_port"), status, display);
            } catch (IOException e) {
                Logger.getLogger(NanoServer.class.getName()).log(Level.SEVERE, null, e);
            }
        });
        websocket.start();

        if (!conf.getString("mqtt_host").isEmpty()) {
            try {
                new MqttClient(conf.getString("mqtt_host"), conf.getInt("mqtt_port"), conf.getString("mqtt_user"), conf.getString("mqtt_pass"), status, display);
            } catch (MqttException e) {
                Logger.getLogger(MqttClient.class.getName()).log(Level.SEVERE, null, e);
            }
        }

        while (true) {
            ControllerServer controller = new ControllerServer(status);
            final boolean[] connected = {true};
            while (connected[0]) {
                if (status.getAnimation() != null) status.getAnimation().stop();
                display.clear();
                status.setGame(null);
                controller.setOnGameSelected(game -> {
                    if (status.getGame() != null) status.getGame().stop();
                    status.setGame(game);
                });

                controller.setOnDisconnectListener(bool -> {
                    connected[0] = false;
                    if (status.getAnimation() != null) new Thread(() -> status.getAnimation().start()).start();
                    status.setUsermac("");
                    status.setUsername("");
                    if (status.getGame() == null) return;
                    status.getGame().stop();
                    status.setGame(null);
                });

                while (status.getGame() == null && connected[0]) {
                    Thread.sleep(50);
                }

                if (status.getGame() == null) continue;
                status.getGame().setDisplay(display);
                status.getGame().setStatus(status);

                controller.setOnKeyChangeListener((key, newState) -> {
                    if (status.getGame() == null) return;
                    status.getGame().sendKey(key, newState);
                });
                controller.setOnKeyHoldListener(key -> {
                    if (status.getGame() == null) return;
                    status.getGame().sendHold(key);
                });

                status.getGame().start();
                display.clear();
            }
        }
    }

    public static Display getDisplay() {
        return display;
    }

    public static NanoWebsocketServer getWebsocketServer() {
        return websocketServer;
    }
}
