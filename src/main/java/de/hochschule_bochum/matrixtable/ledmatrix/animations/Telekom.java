package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 01.08.2017.
 */
public class Telekom implements Animation {

    private boolean running = false;
    private Display display;
    private int[][] onOffs = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 1, 1, 1, 1, 1, 1, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
    };

    public Telekom(Display display) {
        this.display = display;
    }

    // https://github.com/birkir/arduino-ledtable/blob/master/led-table.cpp#L331-L356
    @Override
    public void start() {
        running = true;
        display.clear();
        display.setAll(Color.WHITE);
        for (int y = 0; y < onOffs.length; y++) {
            int[] arrayData = onOffs[y];
            for (int x = 0; x < arrayData.length; x++) {
                if (arrayData[x] == 1) display.set(x + 1, y + 1, new Color(229, 0, 119), false);
            }
        }
        display.update();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
        }
    }

    @Override
    public Animation newInstance(Display display) {
        return new Telekom(display);
    }

    @Override
    public void stop() {
        running = false;
    }

    @Override
    public String getName() {
        return "Telekom";
    }
}
