package de.hochschule_bochum.ledmatrix;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.impl.SpiDeviceImpl;
import de.hochschule_bochum.ledmatrix.objects.Color;
import de.hochschule_bochum.ledmatrix.objects.Display;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(MatrixTest.class.getName()).log(Level.INFO, "Startig Programm");

        SpiDeviceImpl spi = null;
        try {
            spi = new SpiDeviceImpl(SpiChannel.CS0);
        } catch (IOException e) {
            Logger.getLogger(MatrixTest.class.getName()).log(Level.INFO, "Can not find SPI, not on Pi?");
            Logger.getLogger(MatrixTest.class.getName()).log(Level.SEVERE, null, e);
        }

        if (spi == null) return;

        Display display = new Display(5, 1, spi);
        int r = 255;
        int g = 0;
        int b = 0;
        int loops = 255 * 3;
        while (loops > 0) {
            if (r > 0 && b == 0) {
                r--;
                g++;
            }
            if (g > 0 && r == 0) {
                g--;
                b++;
            }
            if (b > 0 && g == 0) {
                r++;
                b--;
            }
            loops--;
            display.setAll(new Color((byte) r, (byte) g, (byte) b));
            Thread.sleep(5);
        }
    }
}