package de.hochschule_bochum.matrixtable.server.webapi;

import de.hochschule_bochum.matrixtable.LEDTable;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import org.glassfish.tyrus.server.Server;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.awt.*;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

@ServerEndpoint(value = "/")
public class WebSocketServer {
    private Session session;
    private Display display;
    private static Set<WebSocketServer> connections = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) throws IOException {
        this.session = session;
        connections.add(this);
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.INFO, null, "WS Connected: " + session.getId());
        display = LEDTable.getDisplay();
        session.getBasicRemote().sendText("Connected!");
    }

    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        Logger.getLogger(WebSocketServer.class.getName()).log(Level.INFO, null, session.getId() + ": " + message);
        JSONObject obj = new JSONObject(message);

        if (!obj.has("command")) {
            return;
        }

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
                    try {
                        session.getBasicRemote().sendText(returnObj.toString());
                    } catch (IOException e) {
                        Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
                    }
                }
                break;
            }

            case "get": {
                JSONObject returnObj = new JSONObject();
                returnObj.put("brightness", display.getGlobalBrightness());
                returnObj.put("length", display.getLength());
                returnObj.put("width", display.getWidth());
                try {
                    session.getBasicRemote().sendText(returnObj.toString());
                } catch (IOException e) {
                    Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
                }
                break;
            }

            case "brightness": {
                if (obj.has("brightness")) {
                    display.setGlobal_brightness(obj.getDouble("brightness"));
                }
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

            default: {

            }
        }
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

    @OnClose
    public void onClose(Session session) {
        connections.remove(this);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        // Do error handling here
    }

    private static void broadcast(String message) {
        connections.forEach(endpoint -> {
            synchronized (endpoint) {
                try {
                    endpoint.session.getBasicRemote().sendText(message);
                } catch (IOException e) {
                    Logger.getLogger(WebSocketServer.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });
    }

    public static Server startServer(Display display) {
        Server wssServer;
        Map<String, Object> properties = Collections.emptyMap();
        wssServer = new Server("0.0.0.0", 8025, "/", properties, WebSocketServer.class);
        return wssServer;
    }

}
