package de.hochschule_bochum.ledmatrix.objects;

/**
 * Created by nikla on 03.07.2017.
 */
public enum Color {
    BLACK,
    BLUE,
    CYAN,
    GREEN,
    ORANGE,
    PURPLE,
    RED,
    YELLOW,
    WHITE;

    public byte[] toByteArray(double brightness) {
        if (brightness < 0 || brightness > 1) throw new IllegalArgumentException("Brightness is not right");
        byte brightnessByte = (byte) (0xE0 | ((int) (31 * brightness)));
        switch (this) {
            // Brightness, Blue, Green, Red
            case BLACK:
                return new byte[]{(byte) 224, (byte) 0, (byte) 0, (byte) 0};
            case BLUE:
                return new byte[]{brightnessByte, (byte) 255, (byte) 0, (byte) 0};
            case CYAN:
                return new byte[]{brightnessByte, (byte) 176, (byte) 176, (byte) 1};
            case GREEN:
                return new byte[]{brightnessByte, (byte) 0, (byte) 255, (byte) 0};
            case ORANGE:
                return new byte[]{brightnessByte, (byte) 0, (byte) 165, (byte) 255};
            case PURPLE:
                return new byte[]{brightnessByte, (byte) 63, (byte) 25, (byte) 242};
            case RED:
                return new byte[]{brightnessByte, (byte) 0, (byte) 0, (byte) 255};
            case YELLOW:
                return new byte[]{brightnessByte, (byte) 0, (byte) 255, (byte) 255};
            case WHITE:
                return new byte[]{brightnessByte, (byte) 255, (byte) 255, (byte) 255};
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
