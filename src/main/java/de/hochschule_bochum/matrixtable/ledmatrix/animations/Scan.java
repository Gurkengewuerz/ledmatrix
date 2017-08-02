package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 01.08.2017.
 */
public class Scan implements Animation {

    private boolean running = false;
    private Display display;
    private boolean forward = true;
    private float saturation = 1.00f;
    private float colorPerc = 0f;
    private int delayMillis = 45;

    public Scan(Display display) {
        this.display = display;
        forward = true;
    }

    /**
     * Scan effect
     * (move one pixel back and forth)
     **/
    @Override
    public void start() {
        running = true;
        display.clear();
        int last_x = 1;
        int last_y = 1;
        while (running) {
            if (forward) {
                for (int y = 1; y <= display.getLength(); y++) {
                    for (int x = 1; x <= display.getWidth(); x++) {
                        Color color = Color.getHSBColor(colorPerc, saturation, 1.0f);
                        display.set(last_x, last_y, Color.BLACK, false);
                        display.set(x, y, color, true);
                        last_x = x;
                        last_y = y;
                        colorPerc += 0.001;
                        try {
                            Thread.sleep(delayMillis);
                        } catch (InterruptedException e) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
            } else {
                for (int y = display.getLength(); y > 0; y--) {
                    for (int x = display.getWidth(); x > 0; x--) {
                        Color color = Color.getHSBColor(colorPerc, saturation, 1.0f);
                        display.set(last_x, last_y, Color.BLACK, false);
                        display.set(x, y, color, true);
                        last_x = x;
                        last_y = y;
                        colorPerc += 0.001;
                        try {
                            Thread.sleep(delayMillis);
                        } catch (InterruptedException e) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                        }
                    }
                }
            }
            forward = !forward;
            try {
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    @Override
    public Animation newInstance(Display display) {
        return new Scan(display);
    }

    @Override
    public void stop() {
        running = false;
    }
}
