package de.hochschule_bochum.matrixtable.engine;

import java.util.ArrayList;

/**
 * Created by nikla on 07.07.2017.
 */
public class GameStatus {
    private static ArrayList<Game> gamelist = new ArrayList<>();
    private Status status = Status.PAUSE;
    private int highscore = 0;
    private int level = 1;
    private int time = 0;
    private TimerType type = TimerType.NONE;
    private String usermac;
    private String username;

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

    public void addGame(Game game) {
        gamelist.add(game);
    }

    public Game getGame(String game) {
        return gamelist.stream().filter(x -> x.getName().equals(game)).findFirst().orElse(null);
    }

    public ArrayList<Game> getGameList() {
        return gamelist;
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

    public void setStatus(Status status) {
        this.status = status;
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

    public void setTime(int time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public TimerType getType() {
        return type;
    }

    public String getUsermac() {
        return usermac;
    }

    public void setUsermac(String usermac) {
        this.usermac = usermac;
    }

    public void reset(Status resetStatus, TimerType type, int time) {
        this.status = resetStatus;
        this.highscore = 0;
        this.level = 1;
        this.time = time;
        this.type = type;
    }

    public static enum Status {
        WAIING,
        WAIING_GAMEOVER,
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
                case WAIING_GAMEOVER:
                    return "waiting_gameover";
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
}
