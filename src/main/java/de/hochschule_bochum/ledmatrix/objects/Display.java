package de.hochschule_bochum.ledmatrix.objects;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.impl.SpiDeviceImpl;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 03.07.2017.
 */
public class Display {

    private int length;
    private int width;
    private double global_brightness;
    private SpiDeviceImpl spi;
    private Color[][] display;
    private boolean debug;

    // Length = Y
    // Width = X
    public Display(int width, int length, boolean debug) {
        this.length = length;
        this.width = width;
        this.debug = debug;

        try {
            spi = new SpiDeviceImpl(SpiChannel.CS0);
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

        global_brightness = 0.5;
        display = new Color[length][width];
        clear();
    }

    public Display(int width, int length) {
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
        Validate.inclusiveBetween(0D, 1D, global_brightness);
        this.global_brightness = global_brightness;
        update();
    }

    public Color get(int x, int y) {
        return display[y - 1][x - 1];
    }

    public void setAll(Color c) {
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                set(x, y, c, false);
            }
        }
        update();
    }

    public void clear() {
        setAll(null);
    }

    public final static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    public void drawAscii() {
        clearConsole();
        for (int y = 1; y <= length; y++) {
            String line = "";
            for (int x = 1; x <= width; x++) {
                line += get(x, y) == null ? "-" : "+";
            }
            System.out.println(y + ":\t" + line);
        }
        System.out.println();
    }

    private void draw() throws IOException {
        byte[] sendingData = new byte[0];
        sendingData = ArrayUtils.add(sendingData, (byte) 0);
        sendingData = ArrayUtils.add(sendingData, (byte) 0);
        sendingData = ArrayUtils.add(sendingData, (byte) 0);
        sendingData = ArrayUtils.add(sendingData, (byte) 0);
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                Color color = get(x, y);
                double brightness = global_brightness;
                if (color == null) {
                    color = new Color(ColorType.BLACK);
                    brightness = 0;
                }
                sendingData = ArrayUtils.addAll(sendingData, color.toByteArray(brightness));
            }
        }
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);

        if (spi != null) spi.write(sendingData);
        if (spi == null || debug) drawAscii();
    }

    public void update() {
        try {
            draw();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
}
