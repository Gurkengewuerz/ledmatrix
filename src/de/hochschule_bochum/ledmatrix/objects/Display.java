package de.hochschule_bochum.ledmatrix.objects;

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


    // Length = Y
    // Width = X
    public Display(int width, int length, SpiDeviceImpl spi) {
        this.length = length;
        this.width = width;
        this.spi = spi;
        global_brightness = 0.5;
        display = new Color[length][width];
        clear();
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

    public void set(int x, int y, Color c) {
        display[y - 1][x - 1] = c;
        update();
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
                set(x, y, c);
            }
        }
    }

    public void clear() {
        setAll(null);
    }

    public void drawAscii() {
        System.out.println();
        for (int y = 1; y <= length; y++) {
            String line = "";
            for (int x = 1; x <= width; x++) {
                line += get(x, y) == null ? "*" : "#";
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
//                System.out.println(DatatypeConverter.printHexBinary(color.toByteArray(brightness)) + " " + color.toString());
            }
        }
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);
        sendingData = ArrayUtils.add(sendingData, (byte) 1);

        spi.write(sendingData, 0, sendingData.length);
    }

    public void update() {
        try {
            draw();
        } catch (IOException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }
}
