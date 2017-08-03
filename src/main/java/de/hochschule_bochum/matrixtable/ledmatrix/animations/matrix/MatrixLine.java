package de.hochschule_bochum.matrixtable.ledmatrix.animations.matrix;

import java.awt.*;
import java.util.Random;

public class MatrixLine {

    private byte length;
    private byte fadingOutTiles;
    private Color firstTileColor;
    private byte y;

    public MatrixLine(Random random) {
        this.length = (byte) (random.nextInt(14) + 5);
        this.fadingOutTiles = (byte) (random.nextInt(1) + 2);

        int closeWhiteColor = random.nextInt(40) + 180;
        this.firstTileColor = new Color(closeWhiteColor, closeWhiteColor, closeWhiteColor);

        this.y = 0;
    }

    public Color[] tick() {
        Color[] line = new Color[20];

        for (int i = 0; i < 20; i++) {
            // Here we skip the empty tiles of the trail
            if (i < y - length) continue;
            // Then we look out for the fading tiles
            if (i < y - (length - fadingOutTiles)) {
                byte fadingStartPos = (byte) (y - length);
                double fadingPos = i - fadingStartPos;
                int fadingGreen = (int) (Math.round((fadingPos / (fadingOutTiles - 1)) * 100) + 75);

                line[i] = new Color(0, fadingGreen, 0);
                continue;
            }
            // Next are the fully green painted tiles
            if (i < y) {
                line[i] = Color.GREEN;
            }
            // And at the end we handle the gray / white tile
            if (i == y) {
                line[i] = firstTileColor;
            }
        }

        y++;

        return line;
    }

    public boolean hasFinished() {
        return y - length - 1 >= 20;
    }
}
