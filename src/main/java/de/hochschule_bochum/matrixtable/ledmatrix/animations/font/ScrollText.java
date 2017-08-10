package de.hochschule_bochum.matrixtable.ledmatrix.animations.font;

import de.hochschule_bochum.matrixtable.ledmatrix.animations.Animation;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 07.08.2017.
 */
public class ScrollText implements Animation {

    protected boolean running = false;
    protected Display display;
    protected Text text;
    protected double speed;
    protected Color color;
    protected String newText = "";

    public ScrollText(Display display) {
        this(display, Color.ORANGE, "");
    }

    public ScrollText(Display display, Color color, String textS) {
        this.display = display;
        this.speed = 0.75D;
        this.color = color;
        this.text = new Text(textS);
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public void setText(String textS) {
        System.out.println("new Text " + textS);
        newText = textS;
    }

    @Override
    public void start() {
        running = true;
        while (running) {
            if (!newText.equals("")) {
                text = new Text(newText);
                newText = "";
            }
            for (int length = -5; length < text.getTextLength(); length++) {
                if (!running || !newText.equals("")) break;
                int[][] data = text.scroll(display.getWidth(), display.getLength(), length);
                for (int i = 0; i < data.length; i++) {
                    if (!running || !newText.equals("")) break;
                    int[] val = data[i];
                    for (int j = 0; j < val.length; j++) {
                        if (!running || !newText.equals("")) break;
                        if (val[j] == 1) display.set(j + 1, i + 1, color, false);
                        else display.set(j + 1, i + 1, null, false);
                    }
                }
                if (!running || !newText.equals("")) break;
                display.update();
                if (length == text.getTextLength() - 1) length = -display.getWidth();
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
        return "Scroll Text";
    }

    @Override
    public Animation newInstance(Display display) {
        return new ScrollText(display);
    }

    @Override
    public void stop() {
        running = false;
    }
}
