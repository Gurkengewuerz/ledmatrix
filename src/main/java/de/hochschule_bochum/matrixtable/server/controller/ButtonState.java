package de.hochschule_bochum.matrixtable.server.controller;

/**
 * Created by nikla on 10.07.2017.
 */
public enum ButtonState {
    BUTTON_UP,
    BUTTON_DOWN;

    public static ButtonState stateFromBoolean(boolean state) {
        if(state) return BUTTON_DOWN;
        return BUTTON_UP;
    }
}
