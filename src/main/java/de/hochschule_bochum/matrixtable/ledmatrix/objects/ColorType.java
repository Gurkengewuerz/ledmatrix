package de.hochschule_bochum.matrixtable.ledmatrix.objects;

/**
 * Created by nikla on 03.07.2017.
 */
public enum ColorType {
    BLACK,
    BLUE,
    CYAN,
    GREEN,
    ORANGE,
    PURPLE,
    RED,
    YELLOW,
    WHITE;

    public byte[] toByteArray() {
        switch (this) {
            // Red, Green, Blue
            case BLACK:
                return new byte[]{(byte) 0, (byte) 0, (byte) 0};
            case BLUE:
                return new byte[]{(byte) 0, (byte) 0, (byte) 255};
            case CYAN:
                return new byte[]{(byte) 1, (byte) 176, (byte) 176};
            case GREEN:
                return new byte[]{(byte) 0, (byte) 255, (byte) 0};
            case ORANGE:
                return new byte[]{(byte) 255, (byte) 165, (byte) 0};
            case PURPLE:
                return new byte[]{(byte) 242, (byte) 25, (byte) 63};
            case RED:
                return new byte[]{(byte) 255, (byte) 0, (byte) 0};
            case YELLOW:
                return new byte[]{(byte) 255, (byte) 255, (byte) 0};
            case WHITE:
                return new byte[]{(byte) 255, (byte) 255, (byte) 255};
            default:
                throw new IllegalArgumentException("Unknown color");
        }
    }

    @Override
    public String toString() {
        switch (this) {
            case BLACK:
                return "Black";
            case BLUE:
                return "Blue";
            case CYAN:
                return "Cyan";
            case GREEN:
                return "Green";
            case ORANGE:
                return "Orange";
            case PURPLE:
                return "Purple";
            case RED:
                return "Red";
            case YELLOW:
                return "Yellow";
            case WHITE:
                return "White";
            default:
                throw new IllegalArgumentException("Unknown color");
        }
    }
}
