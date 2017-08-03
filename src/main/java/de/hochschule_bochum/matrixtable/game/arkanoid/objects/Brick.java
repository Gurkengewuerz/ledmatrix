package de.hochschule_bochum.matrixtable.game.arkanoid.objects;

import de.hochschule_bochum.matrixtable.engine.board.BoardTile;

import java.awt.*;

/**
 * Created by nikla on 31.07.2017.
 */
public class Brick implements BoardTile {
    @Override
    public Color getColor() {
        return Color.YELLOW;
    }
}
