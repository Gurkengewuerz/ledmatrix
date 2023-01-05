package de.hochschule_bochum.matrixtable.engine.game;

import de.hochschule_bochum.matrixtable.engine.Database;
import de.hochschule_bochum.matrixtable.engine.board.Clock;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;
import de.hochschule_bochum.matrixtable.server.controller.ButtonState;
import de.hochschule_bochum.matrixtable.server.controller.Key;

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

    public abstract void start();

    public abstract void updateGame();

    public abstract void reset();

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

    protected abstract void onKey(Key key, ButtonState newState);

    public void sendHold(Key key) {
        // TODO: set Ticks
//        if (masterClock != null) controller.setTimesPerSec(getTickSpeed());
        onHoldListener(key);
    }

    protected void onHoldListener(Key key) {

    }

    public void stop() {
        stoped = true;
        if (status.getStatus() == GameStatus.Status.GAMEOVER)
            status.reset(GameStatus.Status.WAIING_GAMEOVER, GameStatus.TimerType.NONE, 0);
        else {
            status.reset(GameStatus.Status.WAIING, GameStatus.TimerType.NONE, 0);
        }
    }

    protected void gameover() {
        this.gameover = true;
        status.setStatus(GameStatus.Status.GAMEOVER);
        Database.db.executeUpdate("INSERT INTO score (score, user_id, created, game) VALUES (?,(SELECT user_id FROM devices WHERE mac = ?),?,?);", status.getHighscore(), status.getUsermac(), (System.currentTimeMillis() / 1000), getName());
        stop();
    }

    protected void pause() {
        if (getMasterClock().isPaused()) {
            getMasterClock().start();
        } else {
            getMasterClock().pause();
        }
    }

    public abstract Game newInstance();
}
