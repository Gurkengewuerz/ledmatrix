package de.hochschule_bochum.matrixtable.ledmatrix;

import de.hochschule_bochum.matrixtable.background.matrix.MatrixBackground;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.APA102Impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(MatrixTest.class.getName()).log(Level.INFO, "Startig Programm");

        MatrixBackground background = new MatrixBackground(new APA102Impl(10, 20, args.length > 0));
    }
}