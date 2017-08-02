package de.hochschule_bochum.matrixtable.ledmatrix;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.WS2812Impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(MatrixTest.class.getName()).log(Level.INFO, "Startig Programm");

        Examples e = new Examples(new WS2812Impl(60, 1));
        e.RGBFade();
    }
}