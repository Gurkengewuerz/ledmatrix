package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 01.08.2017.
 */
public class Gradient implements Animation {

    private boolean running = false;
    private Display display;
    private float saturation = 1.00f;
    private float colorPerc = 0f;

    public Gradient(Display display) {
        this.display = display;
    }

    // https://github.com/birkir/arduino-ledtable/blob/master/led-table.cpp#L300-L328
    @Override
    public void start() {
        running = true;
        display.clear();
        while (running) {
            for (int r = 1; r <= display.getLength(); r++) {
                for (int c = 1; c <= display.getWidth(); c++) {
                    Color color = Color.getHSBColor((float) (colorPerc + (((float) r / (float) display.getLength()) * 0.25)), saturation, 1f);

                    display.set(c, r, color, false); //
                }
            }
            display.update();
            colorPerc += 0.001;
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    @Override
    public Animation newInstance(Display display) {
        return new Gradient(display);
    }

    @Override
    public void stop() {
        running = false;
    }
}
