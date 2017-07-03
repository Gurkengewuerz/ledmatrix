package de.hochschule_bochum.ledmatrix;

import javax.xml.bind.DatatypeConverter;

/**
 * Created by nikla on 03.07.2017.
 */
public class Display {

    private int length;
    private int width;
    private Colors[][] display;


    // Length = Y
    // Width = X
    public Display(int width, int length) {
        this.length = length;
        this.width = width;
        display = new Colors[length][width];
        clear();
    }

    public void set(int x, int y, Colors c) {
        display[y - 1][x - 1] = c;
    }

    public Colors get(int x, int y) {
        return display[y - 1][x - 1];
    }

    public void clear() {
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                set(x, y, null);
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

    public void draw() {
        byte[] sendingData = new byte[length * width + 8];
        sendingData[0] = (byte) 0;
        sendingData[1] = (byte) 0;
        sendingData[2] = (byte) 0;
        sendingData[3] = (byte) 0;
        for (int y = 1; y <= length; y++) {
            for (int x = 1; x <= width; x++) {
                Colors c = get(x, y);
                if (c == null) c = Colors.BLACK;
                Pixel p = new Pixel();
                p.setColor(c);
                p.setBrightnes(0.5);
                System.out.println(DatatypeConverter.printHexBinary(p.getData()) + " " + c.toString());
            }
        }
    }
}
