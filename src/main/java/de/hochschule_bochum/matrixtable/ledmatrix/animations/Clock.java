package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.animations.font.ScrollText;
import de.hochschule_bochum.matrixtable.ledmatrix.animations.font.Text;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Clock extends ScrollText {
    public Clock(Display display) {
        super(display);
    }

    public Clock(Display display, Color color, String textS) {
        super(display, color, textS);
    }

    public void setText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        Date date = new Date(System.currentTimeMillis());
        text = new Text(sdf.format(date) + " Uhr");
    }

    @Override
    public void start() {
        running = true;
        while (running) {
            setText();
            for (int length = -5; length < text.getTextLength(); length++) {
                if (!running) break;
                int[][] data = text.scroll(display.getWidth(), display.getLength(), length);
                for (int i = 0; i < data.length; i++) {
                    if (!running) break;
                    int[] val = data[i];
                    for (int j = 0; j < val.length; j++) {
                        if (!running) break;
                        if (val[j] == 1) display.set(j + 1, i + 1, color, false);
                        else display.set(j + 1, i + 1, null, false);
                    }
                }
                if (!running) break;
                display.update();
                if (length == text.getTextLength() - 1) {
                    setText();
                    length = -display.getWidth();
                }
                try {
                    Thread.sleep((long) (15 * (100 - speed * 100)));
                } catch (InterruptedException e) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
                }
            }
        }
    }

    @Override
    public String getName() {
        return "Clock";
    }

    @Override
    public Animation newInstance(Display display) {
        return new Clock(display);
    }
}
