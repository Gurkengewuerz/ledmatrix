package de.hochschule_bochum.ledmatrix.objects;

/**
 * Created by nikla on 04.07.2017.
 */
public class Color {
    private byte r;
    private byte g;
    private byte b;

    public Color(byte r, byte g, byte b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(ColorType ct) {
        byte[] data = ct.toByteArray();
        r = data[0];
        g = data[1];
        b = data[2];
    }

    public byte[] toByteArray(double brightness) {
        if (brightness < 0 || brightness > 1) throw new IllegalArgumentException("Brightness is not right");
        byte brightnessByte = (byte) (0xE0 | ((int) (31 * brightness)));
        return new byte[]{brightnessByte, b, g, r};
    }

    @Override
    public String toString() {
        return "Color{" +
                "r=" + (r & 0xFF) +
                ", g=" + (g & 0xFF) +
                ", b=" + (b & 0xFF) +
                '}';
    }
}
