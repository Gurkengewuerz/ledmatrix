package de.hochschule_bochum.ledmatrix;

/**
 * Created by nikla on 04.07.2017.
 */
public class Examples {
    /*
    RGB Dade
     */

//    Display display = new Display(5, 1, spi);
//        display.setAll(new Color(ColorType.PURPLE));
//        display.draw();
//    int r = 255;
//    int g = 0;
//    int b = 0;
//
//        while (true) {
//        if(r > 0 && b == 0){
//            r--;
//            g++;
//        }
//        if(g > 0 && r == 0){
//            g--;
//            b++;
//        }
//        if(b > 0 && g == 0){
//            r++;
//            b--;
//        }
//        display.setAll(new Color((byte) r, (byte) g, (byte) b));
//        display.draw();
//        Thread.sleep(5);
//    }


    /*
    Loop Through all Colors
     */

//    while (true) {
//
//        for (ColorType color : ColorType.values()) {
//            display.setAll(new Color(color));
//            display.draw();
//
//            for (double d = 0D; d <= 1D; d += 0.05) {
//                display.setGlobal_brightness(d);
//                display.draw();
//                Thread.sleep(100);
//            }
//
//            for (double d = 1D; d >= 0D; d -= 0.05) {
//                display.setGlobal_brightness(d);
//                display.draw();
//                Thread.sleep(100);
//            }
//        }
//    }
}
