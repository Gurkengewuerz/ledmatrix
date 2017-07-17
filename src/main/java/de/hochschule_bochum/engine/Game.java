package de.hochschule_bochum.engine;

/**
 * Created by nikla on 15.07.2017.
 */
public interface Game {

    void start();

    void updateGame();

    void reset();

    Clock getMasterClock();

    /*
    Make interface to abstract Class
    Masterclock
    private void onKeyListener(Key key, ButtonState newState);
    protected void onKey(Key key, ButtonState newState);
    private void onHoldListener(Key key);
    set on Hold Listener tickspeed of masterclock
    protected void onHoldListener(Key key);
     */
}
