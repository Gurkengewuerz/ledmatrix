package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 01.08.2017.
 */
public class ScanLine implements Animation {

    private boolean running = false;
    private Display display;
    private boolean forward = true;
    private float saturation = 1.00f;
    private float colorPerc = 0f;
    private int delayMillis = 45;

    public ScanLine(Display display) {
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
        int last_y = 1;
        while (running) {
            if (forward) {
                for (int y = 1; y <= display.getLength(); y++) {
                    if (!running) continue;
                    for (int x = 1; x <= display.getWidth(); x++) {
                        Color color = Color.getHSBColor(colorPerc, saturation, 1.0f);
                        display.set(x, last_y, Color.BLACK, false);
                        display.set(x, y, color, false);
                    }
                    display.update();
                    last_y = y;
                    colorPerc += 0.001;
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                    }
                }
            } else {
                for (int y = display.getLength(); y > 0; y--) {
                    if (!running) continue;
                    for (int x = 1; x <= display.getWidth(); x++) {
                        Color color = Color.getHSBColor(colorPerc, saturation, 1.0f);
                        display.set(x, last_y, Color.BLACK, false);
                        display.set(x, y, color, false);
                    }
                    display.update();
                    last_y = y;
                    colorPerc += 0.001;
                    try {
                        Thread.sleep(delayMillis);
                    } catch (InterruptedException e) {
                        Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
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
        return new ScanLine(display);
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public String getName() {
        return "Scan Line";
    }
}
