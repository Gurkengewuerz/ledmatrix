package de.hochschule_bochum.snake;

import de.hochschule_bochum.engine.Clock;
import de.hochschule_bochum.engine.Direction;
import de.hochschule_bochum.engine.Game;
import de.hochschule_bochum.engine.GameStatus;
import de.hochschule_bochum.server.controller.ButtonState;
import de.hochschule_bochum.server.controller.Key;
import de.hochschule_bochum.snake.objects.SnakeBoard;
import de.hochschule_bochum.snake.objects.SnakePoint;
import de.hochschule_bochum.snake.objects.SnakeTile;
import de.hochschule_bochum.utils.Utils;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 14.07.2017.
 */
public class Snake extends Game {

    private SnakeBoard gameBoard;
    private SnakePoint cherry;
    private SnakeTile snake;
    private Direction direction;

    public void start() {
        reset();

        while (!stoped) {
            if (gameover) break;
            if (masterClock.timeElapsed()) {
                updateGame();
            }

            if (masterClock.isPaused()) {
                status.setStatus(GameStatus.Status.PAUSE);
            } else {
                status.setStatus(GameStatus.Status.RUNNING);
            }

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }

        }
    }

    public void updateGame() {
        if (snake == null) return;
        if (gameover) return;
        Direction lastDirection = direction.copy();
        if (cherry != null) {
            if (snake.getHead().equals(cherry)) {
                status.addHighScore(10);
                tickSpeed += 0.35f;
                masterClock.setTickSpeed(tickSpeed);
                snake.setLength(snake.getLength() + 1);
                spawnCherry();
            }
        }

        snake.move();

        switch (lastDirection) {
            case UP:
                if (snake.getHead().getY() - 1 > 0 && snake.isValid(snake.getHead().getX(), snake.getHead().getY() - 1)) {
                    snake.setHead(new SnakePoint(snake.getHead().getX(), snake.getHead().getY() - 1));
                } else gameover();
                break;
            case DOWN:
                if (snake.getHead().getY() + 1 <= gameBoard.getLength() && snake.isValid(snake.getHead().getX(), snake.getHead().getY() + 1)) {
                    snake.setHead(new SnakePoint(snake.getHead().getX(), snake.getHead().getY() + 1));
                } else gameover();
                break;
            case LEFT:
                if (snake.getHead().getX() - 1 > 0 && snake.isValid(snake.getHead().getX() - 1, snake.getHead().getY())) {
                    snake.setHead(new SnakePoint(snake.getHead().getX() - 1, snake.getHead().getY()));
                } else gameover();
                break;
            case RIGHT:
                if (snake.getHead().getX() + 1 <= gameBoard.getWidth() && snake.isValid(snake.getHead().getX() + 1, snake.getHead().getY())) {
                    snake.setHead(new SnakePoint(snake.getHead().getX() + 1, snake.getHead().getY()));
                } else gameover();
                break;
        }

        if (snake.getSnakeParts().size() > snake.getLength()) {
            snake.removeLast();
        }

        gameBoard.draw(snake, cherry);
    }

    public void reset() {
        gameover = false;
        tickSpeed = 2f;
        masterClock = new Clock(tickSpeed, false);
        masterClock.start();
        gameBoard = new SnakeBoard(display);
        gameBoard.clear();
        status.reset(GameStatus.Status.RUNNING, GameStatus.TimerType.NONE, 0);
        snake = new SnakeTile();
        direction = Direction.DOWN;
        spawnCherry();
    }

    private void spawnCherry() {
        cherry = new SnakePoint(Utils.randInt(1, gameBoard.getWidth()), Utils.randInt(1, gameBoard.getLength()));
    }

    private void move(Direction direction) {
        if (this.direction == Direction.DOWN && direction == Direction.UP) return;
        if (this.direction == Direction.UP && direction == Direction.DOWN) return;
        if (this.direction == Direction.LEFT && direction == Direction.RIGHT) return;
        if (this.direction == Direction.RIGHT && direction == Direction.LEFT) return;
        this.direction = direction;
    }

    @Override
    public String getName() {
        return "Snake";
    }

    @Override
    protected void onKey(Key key, ButtonState newState) {
        switch (key) {
            case LEFT:
                if (newState != ButtonState.BUTTON_DOWN) break;
                move(Direction.LEFT);
                break;

            case RIGHT:
                if (newState != ButtonState.BUTTON_DOWN) break;
                move(Direction.RIGHT);
                break;

            case UP:
                if (newState != ButtonState.BUTTON_DOWN) break;
                move(Direction.UP);
                break;

            case DOWN:
                if (newState == ButtonState.BUTTON_UP)
                    move(Direction.DOWN);
                break;

            case START:
                if (newState != ButtonState.BUTTON_DOWN) break;
                pause();
                break;

            case SELECT:
                if (newState != ButtonState.BUTTON_DOWN) break;
                stop();
                break;
        }
    }

    @Override
    public Game newInstance() {
        return new Snake();
    }
}
