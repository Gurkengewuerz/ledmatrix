package de.hochschule_bochum.server.controller;

import de.hochschule_bochum.GameStatus;
import de.hochschule_bochum.server.Callback;
import de.hochschule_bochum.server.MultiCallback;
import de.hochschule_bochum.server.bluetooth.BluetoothServer;
import org.json.JSONObject;

import javax.bluetooth.RemoteDevice;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

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
        responseHandler = new ResponseHandler();
        btServer.startServer(responseHandler, device -> {
            if (disconnectListener != null) disconnectListener.callback(device);
            responseHandler.close();
            btServer.close();
        });
        this.gameStatus = gameStatus;

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
        btServer.send(json.toString());
    }


    public class ResponseHandler implements Callback<String> {

        private JSONObject jsonData;
        private HashMap<Key, ButtonState> currentStates = new HashMap<>();
        private HashMap<Key, Long> lastSend = new HashMap<>();
        private MultiCallback<Key, ButtonState> keyChangeListener;
        private Callback<Key> keyKeepListener;
        private float tickSpeed;
        private Timer holdTask;

        public ResponseHandler() {
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
            if (!jsonData.has("protocol")) return;
            switch (jsonData.getInt("protocol")) {
                case 1:
                    parseData();
                    break;
            }
        }

        public void setOnKeyChangeListener(MultiCallback<Key, ButtonState> keyChangeListener) {
            this.keyChangeListener = keyChangeListener;
        }

        public void setOnKeyHoldListener(Callback<Key> keyKeepListener) {
            this.keyKeepListener = keyKeepListener;
        }

        public void setTickSpeed(float tickSpeed) {
            this.tickSpeed = tickSpeed;
        }

        public void setTimesPerSec(float timesPerSec) {
            this.tickSpeed = (1.0f / timesPerSec) * 1000;
        }

        public void parseData() {
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
