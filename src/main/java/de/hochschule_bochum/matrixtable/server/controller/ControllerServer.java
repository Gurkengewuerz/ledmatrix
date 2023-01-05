package de.hochschule_bochum.matrixtable.server.controller;

import de.hochschule_bochum.matrixtable.LEDTable;
import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.Manager;
import de.hochschule_bochum.matrixtable.engine.game.Game;
import de.hochschule_bochum.matrixtable.engine.game.GameStatus;
import de.hochschule_bochum.matrixtable.server.Callback;
import de.hochschule_bochum.matrixtable.server.MultiCallback;
import de.hochschule_bochum.matrixtable.server.webapi.NanoWebsocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

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

    private Callback<Boolean> disconnectListener;
    private ResponseHandler responseHandler;
    private GameStatus gameStatus;

    private NanoWebsocketServer.WsdSocket currentClient;

    public ControllerServer(GameStatus gameStatus) {
        this.gameStatus = gameStatus;
        while (LEDTable.getWebsocketServer().getFirstClient() == null) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }

        responseHandler = new ResponseHandler(gameStatus);
        Callback<Boolean> disconnectHandler = device -> {
            if (disconnectListener != null) disconnectListener.callback(true);
            responseHandler.close();
        };

        currentClient = LEDTable.getWebsocketServer().getFirstClient();
        currentClient.startServer(responseHandler, disconnectHandler);

        gameStatus.setUsermac(currentClient.getUserID());
        try {
            ResultSet result = Database.db.executeQuery("SELECT COUNT(*) AS rowcount FROM devices WHERE mac = ?;", gameStatus.getUsermac());
            result.next();
            if (result.getInt("rowcount") == 0)
                Database.db.executeUpdate("INSERT INTO devices (mac, username) VALUES (?, ?);", gameStatus.getUsermac(), currentClient.getUsername());
            result.close();
        } catch (SQLException e) {
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

    public void setOnUnknownProtocolListener(Callback<JSONObject> unknownProtocol) {
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

    public void setOnDisconnectListener(Callback<Boolean> disconnectListener) {
        this.disconnectListener = disconnectListener;
    }

    public void sendStatus(GameStatus status) {
        if (LEDTable.websocketServer == null || currentClient == null) return;
        if (!currentClient.isOpen()) return;
        if (gameStatus == null) return;
        JSONObject gameData = new JSONObject();
        gameData.put("protocol", 2);
        gameData.put("status", status.getStatus().toString());
        gameData.put("level", status.getLevel());
        gameData.put("highscore", status.getHighscore());
        gameData.put("timertype", status.getType().toString());
        gameData.put("timer", status.getTime());
        gameData.put("api_url", status.getApiURL());
        JSONArray games = new JSONArray();
        Manager.getGames().forEach(game -> games.put(game.getName()));
        gameData.put("games", games);

        JSONObject json = new JSONObject();
        json.put("gameData", gameData);

        currentClient.trySend(json.toString());
    }

    public class ResponseHandler implements Callback<JSONObject> {

        private JSONObject jsonData;
        private HashMap<Key, ButtonState> currentStates = new HashMap<>();
        private HashMap<Key, Long> lastSend = new HashMap<>();
        private MultiCallback<Key, ButtonState> keyChangeListener;
        private Callback<Key> keyKeepListener;
        private Callback<JSONObject> unknownProtocol;
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
        public void callback(JSONObject jsonData) {
            this.jsonData = jsonData;
            if (!jsonData.has("protocol")) {
                if (unknownProtocol != null) unknownProtocol.callback(jsonData);
                return;
            }
            switch (jsonData.getInt("protocol")) {
                case 1: // Key Protocol
                    parseKeyData();
                    break;
                case 3: // Settings Protocol
                    String game = jsonData.has("game") ? jsonData.getString("game") : "";
                    Game selectedGame = Manager.getGames().stream().filter(x -> x.getName().equalsIgnoreCase(game)).findFirst().orElse(null);
                    if (selectedGame != null && gameSelected != null) gameSelected.callback(selectedGame.newInstance());
                    String username = jsonData.has("username") ? jsonData.getString("username") : "";
                    status.setUsername(username);
                    if (!status.getUsername().equals("")) {
                        Database.db.executeUpdate("UPDATE devices SET username = ? WHERE mac = ?;", status.getUsername(), status.getUsermac());
                    }
                    break;
                default:
                    if (unknownProtocol != null) unknownProtocol.callback(jsonData);
                    break;
            }
        }

        public void setOnKeyChangeListener(MultiCallback<Key, ButtonState> keyChangeListener) {
            this.keyChangeListener = keyChangeListener;
        }

        public void setOnKeyHoldListener(Callback<Key> keyKeepListener) {
            this.keyKeepListener = keyKeepListener;
        }

        public void setOnUnknownProtocolListener(Callback<JSONObject> unknownProtocol) {
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
