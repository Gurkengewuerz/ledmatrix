package de.hochschule_bochum.matrixtable.server.controller;

import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.Game;
import de.hochschule_bochum.matrixtable.engine.GameStatus;
import de.hochschule_bochum.matrixtable.server.Callback;
import de.hochschule_bochum.matrixtable.server.MultiCallback;
import de.hochschule_bochum.matrixtable.server.bluetooth.BluetoothServer;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.bluetooth.RemoteDevice;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 07.07.2017.
 */
public class ControllerServer {

    private BluetoothServer btServer;
    private Callback<RemoteDevice> disconnectListener;
    private ResponseHandler responseHandler;
    private GameStatus gameStatus;

    public ControllerServer(GameStatus gameStatus) {
        btServer = new BluetoothServer("Remote Device");
        this.gameStatus = gameStatus;
        responseHandler = new ResponseHandler(gameStatus);
        btServer.startServer(responseHandler, device -> {
            if (disconnectListener != null) disconnectListener.callback(device);
            responseHandler.close();
            btServer.close();
        });
        gameStatus.setUsermac(btServer.getDevice().getBluetoothAddress());
        try {
            ResultSet result = Database.db.executeQuery("SELECT COUNT(*) AS rowcount FROM devices WHERE mac = ?;", gameStatus.getUsermac());
            result.next();
            if (result.getInt("rowcount") == 0)
                Database.db.executeUpdate("INSERT INTO devices (mac, username) VALUES (?, ?);", gameStatus.getUsermac(), btServer.getDevice().getFriendlyName(true));
            result.close();
        } catch (IOException | SQLException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                sendStatus(gameStatus);
            }
        }, 0, 5 * 100);
    }

    public void setOnKeyChangeListener(MultiCallback<Key, ButtonState> keyChangeListener) {
        responseHandler.setOnKeyChangeListener(keyChangeListener);
    }

    public void setOnKeyHoldListener(Callback<Key> keyKeepListener) {
        responseHandler.setOnKeyHoldListener(keyKeepListener);
    }

    public void setOnUnknownProtocolListener(Callback<String> unknownProtocol) {
        responseHandler.setOnUnknownProtocolListener(unknownProtocol);
    }

    public void setOnGameSelected(Callback<Game> gameSelected) {
        responseHandler.setOnGameSelected(gameSelected);
    }

    public void setTickSpeed(float tickSpeed) {
        responseHandler.setTickSpeed(tickSpeed);
    }

    public void setTimesPerSec(float timesPerSec) {
        responseHandler.setTimesPerSec(timesPerSec);
    }

    public void setOnDisconnectListener(Callback<RemoteDevice> disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    public void sendStatus(GameStatus status) {
        if (btServer == null) return;
        if (gameStatus == null) return;
        JSONObject json = new JSONObject();
        json.put("protocol", 2);
        json.put("status", status.getStatus().toString());
        json.put("level", status.getLevel());
        json.put("highscore", status.getHighscore());
        json.put("timertype", status.getType().toString());
        json.put("timer", status.getTime());
        JSONArray games = new JSONArray();
        gameStatus.getGameList().forEach(game -> games.put(game.getName()));
        json.put("games", games);
        btServer.send(json.toString());
    }

    public class ResponseHandler implements Callback<String> {

        private JSONObject jsonData;
        private HashMap<Key, ButtonState> currentStates = new HashMap<>();
        private HashMap<Key, Long> lastSend = new HashMap<>();
        private MultiCallback<Key, ButtonState> keyChangeListener;
        private Callback<Key> keyKeepListener;
        private Callback<String> unknownProtocol;
        private Callback<Game> gameSelected;
        private float tickSpeed;
        private Timer holdTask;
        private GameStatus status;

        public ResponseHandler(GameStatus status) {
            this.status = status;
            holdTask = new Timer(true);
            holdTask.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (keyKeepListener == null) return;
                    (new HashMap<>(currentStates)).forEach((key, state) -> {
                        if (state != ButtonState.BUTTON_DOWN) return;
                        if (!lastSend.containsKey(key)) return;
                        long currTime = System.currentTimeMillis();
                        if (currTime - lastSend.get(key) < tickSpeed) return;
                        lastSend.put(key, currTime);
                        keyKeepListener.callback(key);
                    });
                }
            }, 0, 50);
        }

        @Override
        public void callback(String response) {
            jsonData = new JSONObject(response);
            if (!jsonData.has("protocol")) {
                if (unknownProtocol != null) unknownProtocol.callback(response);
                return;
            }
            switch (jsonData.getInt("protocol")) {
                case 1: // Key Protocol
                    parseKeyData();
                    break;
                case 3: // Settings Protocol
                    String game = jsonData.has("game") ? jsonData.getString("game") : "";
                    Game selectedGame = status.getGame(game);
                    if (selectedGame != null && gameSelected != null) gameSelected.callback(selectedGame.newInstance());
                    String username = jsonData.has("username") ? jsonData.getString("username") : "";
                    status.setUsername(username);
                    if (!status.getUsername().equals("")) {
                        Database.db.executeUpdate("UPDATE devices SET username = ? WHERE mac = ?;", status.getUsername(), status.getUsermac());
                    }
                    break;
                default:
                    if (unknownProtocol != null) unknownProtocol.callback(response);
                    break;
            }
        }

        public void setOnKeyChangeListener(MultiCallback<Key, ButtonState> keyChangeListener) {
            this.keyChangeListener = keyChangeListener;
        }

        public void setOnKeyHoldListener(Callback<Key> keyKeepListener) {
            this.keyKeepListener = keyKeepListener;
        }

        public void setOnUnknownProtocolListener(Callback<String> unknownProtocol) {
            this.unknownProtocol = unknownProtocol;
        }

        public void setOnGameSelected(Callback<Game> gameSelected) {
            this.gameSelected = gameSelected;
        }

        public void setTickSpeed(float tickSpeed) {
            this.tickSpeed = tickSpeed;
        }

        public void setTimesPerSec(float timesPerSec) {
            this.tickSpeed = (1.0f / timesPerSec) * 1000;
        }

        public void parseKeyData() {
            parsekey("start");
            parsekey("select");
            parsekey("up");
            parsekey("down");
            parsekey("left");
            parsekey("right");
            parsekey("a");
            parsekey("b");
            parsekey("x");
            parsekey("y");
        }

        public void parsekey(String key) {
            if (!jsonData.has(key)) return;
            Key keyObj = Key.keyToString(key);
            ButtonState newState = ButtonState.stateFromBoolean(jsonData.getBoolean(key));
            ButtonState currentState = currentStates.getOrDefault(keyObj, ButtonState.stateFromBoolean(false));
            if (newState == currentState) return;
            currentStates.put(keyObj, newState);
            if (keyChangeListener != null) {
                keyChangeListener.callback(keyObj, newState);
                lastSend.put(keyObj, System.currentTimeMillis());
            }
        }

        public void close() {
            if (holdTask != null) holdTask.cancel();
        }
    }
}
