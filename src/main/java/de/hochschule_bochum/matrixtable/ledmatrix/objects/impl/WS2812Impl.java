package de.hochschule_bochum.matrixtable.ledmatrix.objects.impl;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.pi3g.pi.ws2812.WS2812;

import java.awt.*;

/**
 * Created by nikla on 28.07.2017.
 */
public class WS2812Impl implements Display {

    /*
     * Using: https://github.com/entrusc/Pi-WS2812
     * Native Lib:
     * https://github.com/tobyweston/unicorn-hat/blob/master/DEVELOPERS.md#raspberry-pi-ws2812-library
     */

    private int length;
    private int width;
    private double global_brightness;
    private Color[][] display;
    private boolean debug;

    /*
    Connect to BCM18 (PWM0) -> Physical Pin 12
     */

    public WS2812Impl(int width, int length, boolean debug) {
        this.length = length;
        this.width = width;
        this.debug = debug;


        WS2812.get().init(width * length);
        WS2812.get().fill(Color.BLACK);
        WS2812.get().show();

        global_brightness = 0.5;
        display = new Color[length][width];
        clear();
    }

    public WS2812Impl(int width, int length) {
        this(width, length, false);
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public double getGlobalBrightness() {
        return global_brightness;
    }

    public void set(int x, int y, Color c, boolean finish) {
        if (x > width || y > length) throw new IllegalArgumentException("x/y is out of range!");
        display[y - 1][x - 1] = c;
        if (finish) update();
    }

    public void set(int x, int y, Color c) {
        set(x, y, c, true);
    }

    public void setGlobal_brightness(double global_brightness) {
        this.global_brightness = global_brightness;
        WS2812.get().setBrightness((float) global_brightness);
    }

    public Color get(int x, int y) {
        return display[y - 1][x - 1];
    }

    public void setAll(Color c) {
        setAll(null, true);
    }

    public void setAll(Color c, boolean update) {
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                set(x, y, c, false);
            }
        }
        if (update) update();
    }

    public void clear() {
        setAll(null);
    }

    public void update() {
        for (int y = 1; y <= length; y++) {
            if (y % 2 == 0) {
                for (int x = width; x >= 1; x--) {
                    Color color = get(x, y);
                    if (color == null) color = new Color(0, 0, 0);
                    WS2812.get().setPixelColor(x * y, color);
                }
            } else {
                for (int x = 1; x <= width; x++) {
                    Color color = get(x, y);
                    if (color == null) color = new Color(0, 0, 0);
                    WS2812.get().setPixelColor(x * y, color);
                }
            }
        }
        WS2812.get().show();
    }

    public int getLEDCount() {
        return length * width;
    }
}
