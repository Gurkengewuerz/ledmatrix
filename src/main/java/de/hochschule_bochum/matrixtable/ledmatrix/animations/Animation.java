package de.hochschule_bochum.matrixtable.ledmatrix.animations;

import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

/**
 * Created by nikla on 01.08.2017.
 */
public interface Animation {

    void stop();

    void start();

    String getName();

    Animation newInstance(Display display);
}
