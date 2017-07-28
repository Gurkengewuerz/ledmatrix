package de.hochschule_bochum.matrixtable.ledmatrix;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.SevenSegment;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 04.07.2017.
 */
public class Examples {

    private Display display;

    public Examples(Display display) {
        this.display = display;
    }

    public void RGBFade() {
        int r = 255;
        int g = 0;
        int b = 0;
        while (true) {
            int loops = 255 * 3;
            while (loops > 0) {
                if (r > 0 && b == 0) {
                    r--;
                    g++;
                }
                if (g > 0 && r == 0) {
                    g--;
                    b++;
                }
                if (b > 0 && g == 0) {
                    r++;
                    b--;
                }
                loops--;
                display.setAll(new Color(r, g, b));
                try {
                    Thread.sleep(5);
                } catch (InterruptedException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
            display.clear();

        }
    }

    public void maxCurrent() {
        display.setAll(Color.WHITE);
        display.setGlobal_brightness(1d);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        display.clear();
    }

    public void sevenSegmentCount() {
        try {
            SevenSegment segement = new SevenSegment(display.getClass());
            for (SevenSegment.Number number : SevenSegment.Number.values()) {
                segement.setNumber(number, Color.BLUE);
                Thread.sleep(1000);
            }
        } catch (InterruptedException | InstantiationException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }

    }
}
