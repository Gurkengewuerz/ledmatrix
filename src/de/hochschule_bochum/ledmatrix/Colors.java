package de.hochschule_bochum.ledmatrix;

/**
 * Created by nikla on 03.07.2017.
 */
public enum Colors {
    BLACK,
    BLUE,
    CYAN,
    GREEN,
    ORANGE,
    PURPLE,
    RED,
    YELLOW;

    public byte[] toByteArray() {
        switch (this) {
            case BLACK:
                return new byte[]{(byte) 0, (byte) 0, (byte) 0};
            case BLUE:
                return new byte[]{(byte) 0, (byte) 0, (byte) 255};
            case CYAN:
                return new byte[]{(byte) 0, (byte) 191, (byte) 255};
            case GREEN:
                return new byte[]{(byte) 0, (byte) 255, (byte) 0};
            case ORANGE:
                return new byte[]{(byte) 255, (byte) 165, (byte) 0};
            case PURPLE:
                return new byte[]{(byte) 128, (byte) 0, (byte) 128};
            case RED:
                return new byte[]{(byte) 255, (byte) 0, (byte) 0};
            case YELLOW:
                return new byte[]{(byte) 255, (byte) 255, (byte) 0};
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
            default:
                throw new IllegalArgumentException("Unknown color");
        }
    }
}
