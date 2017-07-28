package de.hochschule_bochum.matrixtable.ledmatrix.objects;

import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Created by nikla on 04.07.2017.
 */
public class SevenSegment {

    private Display display;
    public enum Number {
        ZERO,
        ONE,
        TWO,
        THREE,
        FOUR,
        FIVE,
        SIX,
        SEVEN,
        EIGHT,
        NINE,
        OFF;

        public boolean[] onOff() {
            switch (this) {
                // Red, Green, Blue
                case ZERO:
                    return new boolean[]{true, true, true, false, true, true, true};
                case ONE:
                    return new boolean[]{false, false, true, false, false, false, true};
                case TWO:
                    return new boolean[]{false, true, true, true, true, true, false};
                case THREE:
                    return new boolean[]{false, true, true, true, false, true, true};
                case FOUR:
                    return new boolean[]{true, false, true, true, false, false, true};
                case FIVE:
                    return new boolean[]{true, true, false, true, false, true, true};
                case SIX:
                    return new boolean[]{true, true, false, true, true, true, true};
                case SEVEN:
                    return new boolean[]{false, true, true, false, false, false, true};
                case EIGHT:
                    return new boolean[]{true, true, true, true, true, true, true};
                case NINE:
                    return new boolean[]{true, true, true, true, false, true, true};
                case OFF:
                    return new boolean[]{false, false, false, false, false, false, false};
                default:
                    throw new IllegalArgumentException("Unknown Number");
            }
        }
    }

    public SevenSegment(Class<?> displayClass) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<?> ctor = displayClass.getConstructor(int.class, int.class);
        display = (Display) ctor.newInstance(7, 1);
    }

    public void setNumber(Number num, Color c) {
        if(display == null) return;
        for (int i = 0; i < num.onOff().length; i++) {
            display.set(i + 1, 1, num.onOff()[i] ? c : null);
        }
    }


}
