package de.hochschule_bochum.ledmatrix.objects;

import com.pi4j.io.spi.impl.SpiDeviceImpl;
import org.apache.commons.lang3.ArrayUtils;

import java.io.IOException;

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

    public double getGlobal_brightness() {
        return global_brightness;
    }

    public void set(int x, int y, Color c) {
        display[y - 1][x - 1] = c;
    }

    public void setGlobal_brightness(double global_brightness) {
        this.global_brightness = global_brightness;
    }

    public Color get(int x, int y) {
        return display[y - 1][x - 1];
    }

    public void clear() {
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                set(x, y, null);
            }
        }
    }

    public void setAll(Color c) {
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                set(x, y, c);
            }
        }
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

    public void draw() throws IOException {
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
                    color = Color.BLACK;
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
}
