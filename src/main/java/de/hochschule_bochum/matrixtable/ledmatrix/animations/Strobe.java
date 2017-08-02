package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 01.08.2017.
 */
public class Strobe implements Animation {

    private boolean running = false;
    private boolean on = false;
    private Display display;

    public Strobe(Display display) {
        this.display = display;
    }

    // https://github.com/birkir/arduino-ledtable/blob/master/led-table.cpp#L331-L356
    @Override
    public void start() {
        running = true;
        display.clear();
        while (running) {
            if(on) display.setAll(Color.WHITE);
            if(!on) display.setAll(Color.BLACK);
            on = !on;
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }
        }

    }

    @Override
    public Animation newInstance(Display display) {
        return new Strobe(display);
    }

    @Override
    public void stop() {
        running = false;
    }
}
