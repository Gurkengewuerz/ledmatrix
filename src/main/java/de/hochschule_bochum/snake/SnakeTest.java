package de.hochschule_bochum.snake;

import de.hochschule_bochum.engine.Direction;
import de.hochschule_bochum.engine.GameStatus;
import de.hochschule_bochum.ledmatrix.objects.Display;
import de.hochschule_bochum.server.controller.ButtonState;
import de.hochschule_bochum.server.controller.ControllerServer;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by nikla on 14.07.2017.
 */
public class SnakeTest {
    public static void main(String[] args) {
        Logger.getLogger(SnakeTest.class.getName()).log(Level.INFO, "Snake LED Started");
        Display display = new Display(10, 20, true);
        GameStatus status = new GameStatus();

        Snake snake = new Snake(display, status);

        ControllerServer controller = new ControllerServer(status);
        controller.setOnKeyChangeListener((key, newState) -> {
            switch (key) {
                case LEFT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    snake.move(Direction.LEFT);
                    break;

                case RIGHT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    snake.move(Direction.RIGHT);
                    break;

                case UP:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    snake.move(Direction.UP);
                    break;

                case DOWN:
                    if (newState == ButtonState.BUTTON_UP)
                        snake.move(Direction.DOWN);
                    break;

                case START:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    if (snake.getMasterClock().isPaused()) {
                        snake.getMasterClock().start();
                    } else {
                        snake.getMasterClock().pause();
                    }
                    break;

                case SELECT:
                    if (newState != ButtonState.BUTTON_DOWN) break;
                    snake.reset();
                    break;
            }
        });

        controller.setOnDisconnectListener(device -> {
            System.exit(1);
        });

        snake.start();
    }
}
