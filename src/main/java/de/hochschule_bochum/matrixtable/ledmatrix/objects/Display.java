package de.hochschule_bochum.matrixtable.ledmatrix.objects;

import java.awt.Color;

/**
 * Created by nikla on 28.07.2017.
 */
public interface Display {

    /**
     * Get Y length
     *
     * @return int Length (Y)
     */
    int getLength();

    /**
     * Get X length
     *
     * @return int Width (X)
     */
    int getWidth();

    double getGlobalBrightness();

    void set(int x, int y, Color c, boolean finish);

    void set(int x, int y, Color c);

    void setGlobal_brightness(double global_brightness);

    Color get(int x, int y);

    void setAll(Color c);

    void setAll(Color c, boolean update);

    void clear();

    void update();
}
