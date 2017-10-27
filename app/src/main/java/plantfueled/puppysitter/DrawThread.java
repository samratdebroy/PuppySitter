package plantfueled.puppysitter;

import android.graphics.Canvas;

/**
 * Created by Simon on 10/27/2017.
 */

public class DrawThread extends Thread {

    private PetSurface surface;

    public DrawThread(PetSurface surface) {
        this.surface = surface;
    }

    @Override
    public void run() {
        super.run();

        long frameTime = (long)(1.0/(30.0/1000.0)); // 30 FPS
        long currentTime = 0;
        long delta = 0;

        long timeTaken = 0;

        while (true) {

            currentTime = System.nanoTime();

            surface.update(delta / 1000000000.0f);

            Canvas c = surface.getHolder().lockCanvas();
            surface.drawScene(c);
            surface.getHolder().unlockCanvasAndPost(c);

            timeTaken = System.nanoTime() - currentTime;

            if (Thread.interrupted()) {
                break;
            }
            try {

                long delay = frameTime - (timeTaken / 1000000);
                if (delay > 0) {
                    Thread.sleep(delay);
                }
            } catch (InterruptedException e) {
                break;
            }

            delta = System.nanoTime() - currentTime;
        }
    }
}
