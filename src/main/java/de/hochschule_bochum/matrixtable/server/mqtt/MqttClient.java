package de.hochschule_bochum.matrixtable.server.mqtt;

import de.hochschule_bochum.matrixtable.engine.Manager;
import de.hochschule_bochum.matrixtable.engine.game.GameStatus;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.Animation;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.font.ScrollText;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by gurkengewuerz.de on 25.02.2019.
 */
public class MqttClient extends org.eclipse.paho.client.mqttv3.MqttClient {

    private Display display;
    private GameStatus gamestatus;

    public MqttClient(String host, int port, String username, String password, GameStatus gamestatus, Display display) throws MqttException {
        super("tcp://" + host + ":" + port, generateClientId());
        Logger.getLogger(MqttClient.class.getName()).log(Level.INFO, "Starting MQTT Client");

        this.display = display;
        this.gamestatus = gamestatus;

        MqttConnectOptions connOpts = new MqttConnectOptions();
        if (username != null && !username.isEmpty()) connOpts.setUserName(username);
        if (password != null && !password.isEmpty()) connOpts.setPassword(password.toCharArray());
        connOpts.setAutomaticReconnect(true);

        connOpts.setWill(
                "home/ledtisch",
                "{\"state\": \"OFF\"}".getBytes(),
                2,
                false);

        connect(connOpts);
        subscribe("home/ledtisch/set");
        sendUpdate();

        /* SAMPLE PAYLOAD:
          {
            "brightness": 120,
            "color": {
              "r": 255,
              "g": 100,
              "b": 100
            },
            "flash": 2,
            "transition": 5,
            "state": "ON"
          }
        */

        setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {
            }

            @Override
            public void messageArrived(String t, MqttMessage m) {
                String payload = new String(m.getPayload());
                try {
                    JSONObject json = new JSONObject(payload);

                    if (json.has("brightness")) {
                        double brightness = (1d / 255d) * json.getInt("brightness");
                        if (brightness >= 0D && brightness <= 1D) display.setGlobal_brightness(brightness);
                        sendUpdate();
                    }

                    if (gamestatus.isPlaying()) return;

                    if (json.has("state")) {
                        String state = json.getString("state");
                        if (state.equalsIgnoreCase("on") && display.getGlobalBrightness() == 0d) {
                            display.setGlobal_brightness(0.5d);
                        } else if (state.equalsIgnoreCase("off")) {
                            display.setGlobal_brightness(0d);
                        }
                        sendUpdate();
                    }

                    if (json.has("effect")) {
                        Animation selectedAnimation = Manager.getAnimations(display).stream().filter(x -> x.getName().equalsIgnoreCase(json.getString("effect"))).findFirst().orElse(null);
                        if (selectedAnimation == null) return;
                        if (gamestatus.getAnimation() != null) gamestatus.getAnimation().stop();
                        gamestatus.setAnimation(selectedAnimation);
                        new Thread(selectedAnimation::start).start();
                        sendUpdate();
                    }

                    if (json.has("color")) {
                        JSONObject colors = json.getJSONObject("color");
                        if (!colors.has("r") && !colors.has("g") && !colors.has("b")) return;

                        if (gamestatus.getAnimation() != null && !(gamestatus.getAnimation() instanceof ScrollText))
                            gamestatus.getAnimation().stop();

                        Color newColor = new Color(colors.getInt("r"), colors.getInt("g"), colors.getInt("b"));

                        if (gamestatus.getAnimation() != null && gamestatus.getAnimation() instanceof ScrollText) {
                            ScrollText scrollText = (ScrollText) gamestatus.getAnimation();
                            scrollText.setColor(newColor);
                        } else display.setAll(newColor);
                        sendUpdate();
                    }
                } catch (JSONException ignored) {

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken t) {
            }
        });
    }

    void sendUpdate() {
        JSONObject data = new JSONObject();
        data.put("state", display.getGlobalBrightness() > 0d ? "ON" : "OFF");
        data.put("brightness", Math.ceil(display.getGlobalBrightness() * 255));

        JSONObject color = new JSONObject();
        Color c = display.get(1, 1);
        if (c == null) c = Color.BLACK;
        color.put("r", c.getRed());
        color.put("g", c.getGreen());
        color.put("b", c.getBlue());
        data.put("color", color);

        try {
            publish("home/ledtisch", data.toString().getBytes(), 2, true);
        } catch (MqttException e) {
            Logger.getLogger(MqttClient.class.getName()).log(Level.SEVERE, null, e);
        }
    }
}
