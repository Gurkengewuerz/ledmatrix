package de.hochschule_bochum.ledmatrix.objects;

/**
 * Created by nikla on 04.07.2017.
 */
public class SevenSegment extends Display {
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

    public SevenSegment() {
        super(7, 1);
    }

    public void setNumber(Number num, Color c) {
        for (int i = 0; i < num.onOff().length; i++) {
            set(i + 1, 1, num.onOff()[i] ? c : null);
        }
    }


}
