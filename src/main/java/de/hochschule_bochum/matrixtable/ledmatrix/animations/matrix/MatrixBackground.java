package de.hochschule_bochum.matrixtable.ledmatrix.animations.matrix;

import de.hochschule_bochum.matrixtable.ledmatrix.animations.Animation;
import de.hochschule_bochum.matrixtable.ledmatrix.objects.Display;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MatrixBackground implements Animation {

    private Display display;
    private boolean aborted;
    private MatrixLine[] lines;

    public MatrixBackground(Display display) {
        this.display = display;
        this.lines = new MatrixLine[10];
    }

    private void loop() {
        // We predefine the variables here, because we need them only one instance of them and it's faster this way.
        long startTime, elapsedNanos, sleepTime;
        double elapsedMilis;

        while (!Thread.interrupted() && !aborted) {
            // This mechanism of mearusing time should help to keep a consistent framerate of 100 frames per second.
            startTime = System.nanoTime();
            render();
            elapsedNanos = System.nanoTime() - startTime;
            try {
                elapsedMilis = elapsedNanos / 1000.0 / 1000.0;
                // System.out.printf("Time: %f\n", elapsedMilis); <- This is for debugging the frame time
                // The lowest working frame time is about 25 milliseconds
                sleepTime = Math.round(100 - elapsedMilis);
                if (sleepTime > 0) Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void render() {
        display.setAll(null, false);
        List<Byte> emptyLines = new ArrayList<>(10);

        // Draw the existing lines
        for (byte x = 0; x < 10; x++) {
            MatrixLine matrixLine = lines[x];
            if (matrixLine != null) {
                // Draw the pixels of the current line
                drawLine(x, matrixLine);
                // Check whether the line is out of the screen and if so, remove it
                if (matrixLine.hasFinished()) {
                    lines[x] = null;
                    //emptyLines.add(x);
                }
            } else {
                emptyLines.add(x);
            }
        }

        // Select a random line, save it and draw it
        if (emptyLines.size() > 0) {
            Random random = new Random();
            byte lineX = (byte) random.nextInt(10);
            // If at the random position isn't a line, we add one
            if (lines[lineX] == null) {
                MatrixLine line = new MatrixLine(random);
                lines[lineX] = line;
                drawLine(lineX, line);
            }
        }

        // And finally we draw everything
        display.update();
    }

    private void drawLine(byte x, MatrixLine matrixLine) {
        Color[] colors = matrixLine.tick();
        for (byte y = 0; y < 20; y++) {
            Color color = colors[y];
            if (color != null) display.set(x + 1, y + 1, color, false);
        }
    }

    private void abort() {
        aborted = true;
    }

    // The animation interface implementation

    @Override
    public Animation newInstance(Display display) {
        return new MatrixBackground(display);
    }

    @Override
    public void start() {
        aborted = false;
        loop();
    }

    @Override
    public void stop() {
        abort();
    }
}
