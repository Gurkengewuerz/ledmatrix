package de.hochschule_bochum.engine;

/**
 * Created by nikla on 07.07.2017.
 */
public class GameStatus {
    public static enum Status {
        WAIING,
        PAUSE,
        GAMEOVER,
        RUNNING;

        public String toString() {
            switch (this) {
                case WAIING:
                    return "waiting";
                case PAUSE:
                    return "pause";
                case GAMEOVER:
                    return "gameover";
                case RUNNING:
                    return "running";
            }
            return "";
        }
    }

    public static enum TimerType {
        COUNTDOWN,
        COUNTER,
        NONE;

        public String toString() {
            switch (this) {
                case COUNTDOWN:
                    return "countdown";
                case COUNTER:
                    return "counter";
                case NONE:
                    return "none";
            }
            return "";
        }
    }

    private Status status = Status.PAUSE;
    private int highscore = 0;
    private int level = 1;
    private int time = 0;
    private TimerType type = TimerType.NONE;

    public GameStatus(Status status, int highscore, int level, int time, TimerType type) {
        this.status = status;
        this.highscore = highscore;
        this.level = level;
        this.time = time;
        this.type = type;
    }

    public GameStatus() {
        this(Status.WAIING, 0, 1, 0, TimerType.NONE);
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void levelUp() {
        level++;
    }

    public void addHighScore(int score) {
        highscore = highscore + score;
    }

    public Status getStatus() {
        return status;
    }

    public int getHighscore() {
        return highscore;
    }

    public int getLevel() {
        return level;
    }

    public int getTime() {
        return time;
    }

    public TimerType getType() {
        return type;
    }

    public void reset(Status resetStatus, TimerType type, int time) {
        this.status = resetStatus;
        this.highscore = 0;
        this.level = 1;
        this.time = time;
        this.type = type;
    }
}
