package de.hochschule_bochum.ledmatrix;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(MatrixTest.class.getName()).log(Level.INFO, "Startig Programm");

        Examples e = new Examples();
        e.maxCurrent();
    }
}