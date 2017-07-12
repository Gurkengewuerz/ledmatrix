package de.hochschule_bochum.ledmatrix;

import com.pi4j.io.spi.SpiChannel;
import com.pi4j.io.spi.impl.SpiDeviceImpl;

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
        Examples e = new Examples(spi);
        e.colorLoop();
    }
}