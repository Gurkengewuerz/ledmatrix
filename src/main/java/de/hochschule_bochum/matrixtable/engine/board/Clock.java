package de.hochschule_bochum.matrixtable.engine.board;

/**
 * Created by nikla on 04.07.2017.
 */
public class Clock {

    private float tickSpeed;
    private long lastUpdate;
    private boolean paused;

    public Clock(float ticksPerSecond, boolean startPaused) {
        setTickSpeed(ticksPerSecond);
        paused = startPaused;
        lastUpdate = System.currentTimeMillis();
    }

    public Clock(float ticksPerSecond) {
        setTickSpeed(ticksPerSecond);
        paused = false;
        lastUpdate = System.currentTimeMillis();
    }

    public void setTickSpeed(float tickSpeed) {
        this.tickSpeed = (1.0f / tickSpeed) * 1000;
    }

    public void start() {
        this.paused = false;
    }

    public void pause() {
        this.paused = true;
    }

    public boolean isPaused() {
        return paused;
    }

    public boolean timeElapsed() {
        if (paused) return false;
        long currTime = System.currentTimeMillis();
        if (currTime - lastUpdate < tickSpeed) return false;
        lastUpdate = currTime;
        return true;

    }
}
