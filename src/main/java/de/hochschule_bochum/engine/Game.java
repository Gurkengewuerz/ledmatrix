package de.hochschule_bochum.engine;

import de.hochschule_bochum.ledmatrix.objects.Display;
import de.hochschule_bochum.server.controller.ButtonState;
import de.hochschule_bochum.server.controller.Key;

/**
 * Created by nikla on 15.07.2017.
 */
public abstract class Game {

    protected Clock masterClock;
    protected float tickSpeed;
    protected boolean gameover;
    protected Display display;
    protected GameStatus status;
    protected boolean stoped;

    public void setDisplay(Display display) {
        this.display = display;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public void start() {

    }

    public void updateGame() {

    }

    public void reset() {

    }

    public Clock getMasterClock() {
        return masterClock;
    }

    public float getTickSpeed() {
        return tickSpeed;
    }

    public String getName() {
        return "unknown";
    }

    public void sendKey(Key key, ButtonState newState) {
        onKey(key, newState);
    }

    protected void onKey(Key key, ButtonState newState) {

    }

    public void sendHold(Key key) {
        // TODO: set Ticks
//        if (masterClock != null) controller.setTimesPerSec(getTickSpeed());
        onHoldListener(key);
    }

    protected void onHoldListener(Key key) {

    }

    public void stop() {
        stoped = true;
        status.reset(GameStatus.Status.WAIING, GameStatus.TimerType.NONE, 0);
    }

    protected void gameover() {
        this.gameover = true;
        status.setStatus(GameStatus.Status.GAMEOVER);
        Database.db.executeQuery("INSERT INTO score (score, user_id, created, game) VALUES (" +
                "'" + SQLInjectionEscaper.escapeString(String.valueOf(status.getHighscore()), false) + "'," +
                "(SELECT user_id FROM devices WHERE mac = '"+ SQLInjectionEscaper.escapeString(status.getUsermac(), false) +"')," +
                "'" + SQLInjectionEscaper.escapeString(String.valueOf(System.currentTimeMillis() / 1000), false) + "'," +
                "'" + SQLInjectionEscaper.escapeString(getName(), false) + "'" +
                ");");
    }

    protected void pause() {
        if (getMasterClock().isPaused()) {
            getMasterClock().start();
        } else {
            getMasterClock().pause();
        }
    }

    public Game newInstance() {
        return null;
    }
}
