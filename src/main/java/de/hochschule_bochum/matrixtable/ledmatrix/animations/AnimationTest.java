package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.impl.APA102Impl;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 02.07.2017.
 */
public class AnimationTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        Logger.getLogger(AnimationTest.class.getName()).log(Level.INFO, "Startig Programm");
        Display d = new APA102Impl(10, 20);
        Animation effect = new Telekom(d);
        effect.start();
    }
}
