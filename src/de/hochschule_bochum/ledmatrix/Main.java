package de.hochschule_bochum.ledmatrix;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.impl.SpiDeviceImpl;
import de.hochschule_bochum.ledmatrix.objects.Color;
import de.hochschule_bochum.ledmatrix.objects.Display;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(Main.class.getName()).log(Level.INFO, "Startig Programm");

        SpiDeviceImpl spi = null;
        try {
            spi = new SpiDeviceImpl(SpiChannel.CS0);
        } catch (IOException e) {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Can not find SPI, not on Pi?");
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, e);
        }

        if (spi == null) return;

        Display display = new Display(5, 1, spi);
        display.setAll(Color.RED);

        while (true) {
            for (Color color : Color.values()) {
                display.setAll(color);
                display.draw();

                for (double d = 0D; d <= 1D; d += 0.05) {
                    display.setGlobal_brightness(d);
                    display.draw();
                    Thread.sleep(100);
                }

                for (double d = 1D; d >= 0D; d -= 0.05) {
                    display.setGlobal_brightness(d);
                    display.draw();
                    Thread.sleep(100);
                }
            }
        }


//        for (Color color : Color.values()) {
//            for (int y = 1; y <= display.getLength(); y++) {
//                for (int x = 1; x <= display.getWidth(); x++) {
//                    display.set(x, y, color);
//                    display.draw();
//                    Thread.sleep(100);
//                }
//            }
//        }
//        display.drawAscii();
    }
}