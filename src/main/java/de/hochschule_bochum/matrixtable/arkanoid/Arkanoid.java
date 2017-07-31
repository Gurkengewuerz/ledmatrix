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

    /*
    TODO: Fix Ball Glitching between bricks
    TODO: Fix Ball Glitching at paddle

     */

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

            gameBoard.draw(ball, paddle);
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
            moveBall();
            check();
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
        ball = new Ball(gameBoard.getWidth() / 2, gameBoard.getLength() - 5, gameBoard.getWidth() - 1, gameBoard.getLength() - 1);
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

    private void moveBall() {
        ball.move();
    }

    private void check() {
        if (ball.getY() == paddle.getY()) gameover(); // Remove for Debug

        if (ball.getY() <= 0 || ball.getY() >= gameBoard.getLength() - 1) {
            ball.setMoveY(!ball.isMoveY());
        } else if (paddle.getY() == ball.getNextY() && (ball.getNextX() >= paddle.getX() && ball.getNextX() <= paddle.getX() + paddle.getSize())) {
            ball.setMoveY(!ball.isMoveY());
        }

        boolean hit = false;
        int countCleared = 0;
        if (gameBoard.getTile(ball.getX(), ball.getNextY()) != null) {
            gameBoard.setTile(null, ball.getX(), ball.getNextY());
            hit = true;
            countCleared++;
        }
        if (ball.getX() > 0 && gameBoard.getTile(ball.getX() - 1, ball.getY()) != null) {
            gameBoard.setTile(null, ball.getX() - 1, ball.getNextY());
            hit = true;
            countCleared++;
        }
        if (ball.getX() < gameBoard.getWidth() - 1 && gameBoard.getTile(ball.getX() + 1, ball.getY()) != null) {
            gameBoard.setTile(null, ball.getX() + 1, ball.getY());
            hit = true;
            countCleared++;
        }


        if (!hit && gameBoard.getTile(ball.getNextX(), ball.getNextY()) != null) {
            gameBoard.setTile(null, ball.getNextX(), ball.getNextY());
            hit = true;
            countCleared++;
        }
        if (hit) {
            tickSpeed += 0.05f;
            ball.setMoveY(!ball.isMoveY());
            status.addHighScore(100 * countCleared);
        }

        if (ball.getX() <= 0 || ball.getX() >= gameBoard.getWidth() - 1)
            ball.setMoveX(!ball.isMoveX());
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
}
