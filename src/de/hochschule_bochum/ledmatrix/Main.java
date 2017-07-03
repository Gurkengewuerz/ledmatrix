package de.hochschule_bochum.ledmatrix;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    public static void main(String[] args) {
            Logger.getLogger(Main.class.getName()).log(Level.INFO, "Startig Programm");
            Display d = new Display(10, 20);
            d.set(1,1,Colors.CYAN);
            d.set(1,3,Colors.CYAN);
            d.set(2,2,Colors.CYAN);
            d.set(3,3,Colors.CYAN);
            d.set(3,1,Colors.CYAN);
            System.out.print(d.get(1,2));
            d.drawAscii();
            d.draw();
    }
}