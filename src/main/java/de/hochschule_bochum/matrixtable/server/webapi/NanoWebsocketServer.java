package de.hochschule_bochum.matrixtable.server.webapi;

import de.hochschule_bochum.matrixtable.engine.game.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.server.Callback;
import fi.iki.elonen.NanoWSD;
import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.01.2023.
 */
public class NanoWebsocketServer extends NanoWSD {
    protected static final HashMap<UUID, WsdSocket> clients = new HashMap<>();
    private final GameStatus gamestatus;
    private final Display display;

    public NanoWebsocketServer(int port, GameStatus gamestatus, Display display) throws IOException {
        super(port);
        this.gamestatus = gamestatus;
        this.display = display;
        start(60 * 1000, false);
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession ihttpSession) {
        return new WsdSocket(ihttpSession, gamestatus, display);
    }

    public WsdSocket getFirstClient() {
        if (clients.isEmpty()) return null;
        return clients.values().stream().filter(sock -> !sock.getUserID().isEmpty() && !sock.getUsername().isEmpty()).findFirst().orElse(null);
    }

    public void broadcast(String payload) {
        clients.forEach((id, wsd) -> {
            try {
                wsd.send(payload);
            } catch (IOException ignored) {
            }
        });
    }

    public static class WsdSocket extends WebSocket {
        private final UUID uuid;
        private final GameStatus gamestatus;
        private final Display display;

        private Callback<Boolean> disconnectListener;
        private Callback<JSONObject> receiveCallback;

        private String userID = "";
        private String username = "";

        public WsdSocket(IHTTPSession handshakeRequest, GameStatus gamestatus, Display display) {
            super(handshakeRequest);
            this.uuid = UUID.randomUUID();
            this.gamestatus = gamestatus;
            this.display = display;
        }

        @Override
        protected void onOpen() {
            Logger.getLogger(NanoWebsocketServer.class.getName()).log(Level.INFO, this.uuid.toString() + " connected");
            NanoWebsocketServer.clients.put(this.uuid, this);
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode closeCode, String s, boolean b) {
            Logger.getLogger(NanoWebsocketServer.class.getName()).log(Level.INFO, this.uuid.toString() + " disconnected");
            NanoWebsocketServer.clients.remove(this.uuid);
            if (disconnectListener != null) disconnectListener.callback(true);
        }

        public void startServer(Callback<JSONObject> receiveCallback, Callback<Boolean> disconnectCallback) {
            this.disconnectListener = disconnectCallback;
            this.receiveCallback = receiveCallback;
        }

        public void trySend(String payload) {
            try {
                send(payload);
            } catch (IOException ignored) {
            }
        }

        @Override
        protected void onMessage(WebSocketFrame webSocketFrame) {
            Logger.getLogger(NanoWebsocketServer.class.getName()).log(Level.INFO, "New WS message " + webSocketFrame.getTextPayload());
            JSONObject obj = new JSONObject(webSocketFrame.getTextPayload());

            if (!obj.has("command")) return;

            switch (obj.getString("command").toLowerCase()) {
                case "getpixel": {
                    if (obj.has("x") && obj.has("y")) {
                        JSONObject returnObj = new JSONObject();
                        returnObj.put("x", obj.getInt("x"));
                        returnObj.put("y", obj.getInt("y"));

                        JSONArray array = new JSONArray();
                        Color c = display.get(obj.getInt("x"), obj.getInt("y"));
                        array.put(c.getRed());
                        array.put(c.getGreen());
                        array.put(c.getBlue());
                        returnObj.put("rgb", array);
                        trySend(returnObj.toString());
                    }
                    break;
                }

                case "get": {
                    JSONObject returnObj = new JSONObject();
                    returnObj.put("brightness", display.getGlobalBrightness());
                    returnObj.put("length", display.getLength());
                    returnObj.put("width", display.getWidth());
                    trySend(returnObj.toString());
                    break;
                }

                case "brightness": {
                    if (obj.has("brightness")) display.setGlobal_brightness(obj.getDouble("brightness"));
                    break;
                }

                case "clear": {
                    display.clear();
                    break;
                }

                case "force_update": {
                    display.update();
                    break;
                }

                case "setsingle": {
                    setPixel(obj);
                    break;
                }

                case "setmultiple": {
                    Iterator<?> keys = obj.keys();

                    while (keys.hasNext()) {
                        String key = (String) keys.next();
                        if (obj.get(key) instanceof JSONObject) {
                            JSONObject pair = obj.getJSONObject(key);
                            setPixel(pair);
                        }
                    }
                    break;
                }

                case "fill": {
                    if (obj.has("rgb")) {
                        JSONArray array = obj.getJSONArray("rgb");
                        if (array.length() < 2) return;

                        display.setAll(new Color(
                                array.getInt(0),
                                array.getInt(1),
                                array.getInt(2)), true);
                    }
                    break;
                }

                case "game": {
                    if (this.receiveCallback != null) this.receiveCallback.callback(obj.getJSONObject("game"));
                    break;
                }

                case "user": {
                    if (obj.has("username")) username = obj.getString("username");
                    if (obj.has("uuid")) userID = obj.getString("uuid");
                    break;
                }

                case "ping": {
                    JSONObject returnObj = new JSONObject();
                    returnObj.put("command", "pong");
                    trySend(returnObj.toString());
                    break;
                }

                default: {

                }
            }
        }

        @Override
        protected void onPong(WebSocketFrame webSocketFrame) {

        }

        @Override
        protected void onException(IOException e) {
            System.out.println(e.getMessage());
        }

        public String getUserID() {
            return userID;
        }

        public String getUsername() {
            return username;
        }

        private void setPixel(JSONObject obj) {
            if (obj.has("x") && obj.has("y") && obj.has("rgb")) {
                JSONArray array = obj.getJSONArray("rgb");
                if (array.length() < 2) return;

                display.set(
                        obj.getInt("x"),
                        obj.getInt("y"),
                        new Color(
                                array.getInt(0),
                                array.getInt(1),
                                array.getInt(2))
                );
            }
        }
    }

}
