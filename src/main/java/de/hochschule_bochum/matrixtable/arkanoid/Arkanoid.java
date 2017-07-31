package de.hochschule_bochum.matrixtable.arkanoid;

import de.hochschule_bochum.matrixtable.arkanoid.objects.ArkanoidBoard;
import de.hochschule_bochum.matrixtable.arkanoid.objects.Ball;
import de.hochschule_bochum.matrixtable.arkanoid.objects.Brick;
import de.hochschule_bochum.matrixtable.arkanoid.objects.Paddle;
import de.hochschule_bochum.matrixtable.engine.Clock;
import de.hochschule_bochum.matrixtable.engine.Direction;
import de.hochschule_bochum.matrixtable.engine.Game;
import de.hochschule_bochum.matrixtable.engine.GameStatus;
import de.hochschule_bochum.matrixtable.server.controller.ButtonState;
import de.hochschule_bochum.matrixtable.server.controller.Key;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 31.07.2017.
 */
public class Arkanoid extends Game {

    private ArkanoidBoard gameBoard;
    private Paddle paddle;
    private Ball ball;

    @Override
    public void start() {
        reset();

        while (!stoped) {
            if (gameover) break;

            if (masterClock.isPaused()) {
                status.setStatus(GameStatus.Status.PAUSE);
            } else {
                status.setStatus(GameStatus.Status.RUNNING);
            }

            updateGame();

            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, e);
            }

        }
    }

    @Override
    public void updateGame() {
        if (masterClock.isPaused()) return;
        if (masterClock.timeElapsed()) {
            checkBallHitByPaddle();
            checkBlockCollision();
            ball.move();
            checkBallOutOfBoundsTable();
            gameBoard.draw(ball, paddle);
        }
    }

    @Override
    public void reset() {
        if (display == null) throw new NullPointerException("Display is not set.");
        gameover = false;
        tickSpeed = 2.5f;
        masterClock = new Clock(tickSpeed, false);
        gameBoard = new ArkanoidBoard(display);
        paddle = new Paddle(gameBoard.getWidth() / 2, gameBoard.getLength() - 3, 3);
        ball = new Ball(gameBoard.getWidth() / 2, gameBoard.getLength() - 5);
        ball.setX(gameBoard.getWidth() / 2);
        ball.setY(gameBoard.getLength() / 2);
        for (int i = 0; i < gameBoard.getWidth(); i++) {
            for (int height = 0; height < 4; height++) {
                gameBoard.setTile(new Brick(), i, 1 + height);
            }
        }
        status.reset(GameStatus.Status.RUNNING, GameStatus.TimerType.NONE, 0);
    }

    private void move(Direction direction) {
        if (direction == Direction.LEFT) {
            if (masterClock.isPaused()) return;
            if (paddle.getX() <= 0) return;
            paddle.moveLeft();
        } else if (direction == Direction.RIGHT) {
            if (masterClock.isPaused()) return;
            if (paddle.getX() + paddle.getSize() >= gameBoard.getWidth()) return;
            paddle.moveRight();
        }
    }

    @Override
    protected void onKey(Key key, ButtonState newState) {
        if (newState == ButtonState.BUTTON_UP) return;
        switch (key) {
            case UP:
            case RIGHT:
                move(Direction.RIGHT);
                break;

            case DOWN:
            case LEFT:
                move(Direction.LEFT);
                break;

            case START:
                if (newState != ButtonState.BUTTON_DOWN) break;
                if (getMasterClock().isPaused()) {
                    getMasterClock().start();
                } else {
                    getMasterClock().pause();
                }
                break;

            case SELECT:
                if (newState != ButtonState.BUTTON_DOWN) break;
                stop();
                break;
        }
    }

    @Override
    public Game newInstance() {
        return new Arkanoid();
    }

    @Override
    public String getName() {
        return "Arkanoid";
    }

    private void checkBallHitByPaddle() {
        if (ball.getY() + 1 != paddle.getY()) return;
        if (ball.getX() == paddle.getX()) { // Hit left
            ball.setIncrementY(-1);
            ball.setY(paddle.getY() - 1);
            ball.setIncrementX(Math.max(-1, ball.getIncrementX() - 1));
        } else if (ball.getX() == paddle.getX() + paddle.getSize() - 1) { // hit right
            ball.setIncrementY(-1);
            ball.setY(paddle.getY() - 1);
            ball.setIncrementX(Math.min(1, ball.getIncrementX() + 1));
        } else if (ball.getX() > paddle.getX() && ball.getX() < paddle.getX() + paddle.getSize() - 1) {
            ball.setIncrementY(-1);
            ball.setIncrementX(0);
            ball.setY(paddle.getY() - 1);
        }
    }

    private void checkBallOutOfBoundsTable() {
        if (ball.getY() < 0) {
            ball.setIncrementY(-ball.getIncrementY());
            ball.setY(1);
        } else if (ball.getY() > gameBoard.getLength() - 2) {
            ball.setIncrementY(-ball.getIncrementY());
            ball.setIncrementX(0);
            ball.setY(gameBoard.getLength() / 2);
            ball.setX(gameBoard.getWidth() / 2);
            gameover();
        }
        if (ball.getX() < 0) {
            ball.setIncrementX(-ball.getIncrementX());
            ball.setX(1);
        } else if (ball.getX() > gameBoard.getWidth() - 1) {
            ball.setIncrementX(-ball.getIncrementX());
            ball.setX(gameBoard.getWidth() - 2);
        }
    }

    private boolean checkBlockCollision() {
        int ballTop = ball.getY() - 1;
        int ballBottom = ball.getY() + 1;
        int ballLeft = ball.getX() - 1;
        int ballRight = ball.getX() + 1;

        for (int y = 0; y < gameBoard.getLength(); y++) {
            for (int x = 0; x < gameBoard.getWidth(); x++) {
                if (gameBoard.getTile(x, y) == null) continue;
                if (ballBottom >= y && ballTop <= y) {
                    if (ballRight >= x && ballLeft <= x) {
                        gameBoard.setTile(null, x, y);
                        status.addHighScore(100);
                        ball.setIncrementY(-ball.getIncrementY());
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
