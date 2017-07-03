package de.hochschule_bochum.ledmatrix;

/**
 * Created by nikla on 03.07.2017.
 */
public class Pixel {
    private Colors c;
    private double brightnes;

    public Pixel() {
        this.c = Colors.BLACK;
        this.brightnes = 1D;
    }

    public void setColor(Colors c) {
        this.c = c;
    }

    public void setBrightnes(double brightnes) {
        this.brightnes = brightnes;
    }

    public Colors getColor() {
        return c;
    }

    public double getBrightnes() {
        return brightnes;
    }

    public byte[] getData() {
        byte[] dataArray = new byte[4];
        if (brightnes < 0 || brightnes > 1) throw new IllegalArgumentException("Brightness is not right");
        dataArray[0] = (byte) (int) (255 * brightnes);
        byte[] color = c.toByteArray();
        dataArray[1] = color[0];
        dataArray[2] = color[1];
        dataArray[3] = color[2];
        return dataArray;
    }
}
