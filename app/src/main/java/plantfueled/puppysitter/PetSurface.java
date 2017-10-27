package plantfueled.puppysitter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Simon on 10/27/2017.
 */

public class PetSurface extends SurfaceView implements SurfaceHolder.Callback {

    private Paint skyPaint;
    private Paint grassPaint;
    private Paint fencePaint;
    private Paint cloudPaint;
    private Paint petNamePaint;

    private ArrayList<Rect> clouds;
    private float cloudCounter;

    private DrawThread drawThread;

    private Pet pet;

    public PetSurface(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        skyPaint = new Paint();
        skyPaint.setColor(Color.parseColor("#87CEFA"));

        grassPaint = new Paint();
        grassPaint.setColor(Color.parseColor("#7CFC00"));

        fencePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        fencePaint.setColor(Color.parseColor("#7F3F00"));

        cloudPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        cloudPaint.setColor(Color.WHITE);

        petNamePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        petNamePaint.setColor(Color.WHITE);
        petNamePaint.setTextSize(64);

        clouds = new ArrayList<Rect>();

        getHolder().addCallback(this);
    }

    public void setPet(Pet pet) {
        this.pet = pet;
    }

    public void update(float deltaSeconds) {
        //Log.i("PUPPYSITTER", String.valueOf(1.0 / deltaSeconds));

        // Spawn cloud every 2 seconds
        cloudCounter += deltaSeconds;
        if (cloudCounter >= 2) {
            Random rand = new Random();

            int cloudX = getRight();
            int cloudY = (int)(getHeight() * 0.2 * rand.nextFloat());
            clouds.add(new Rect(cloudX, cloudY, cloudX + 250, cloudY + 50));
            Log.i("PUPPYSITTER", "Cloud spawned");
            cloudCounter = 0;
        }

        // Translate clouds
        for (Rect cloud : clouds) {
            cloud.offset((int)(-100 * deltaSeconds), 0);
        }

        // Check for cloud removal
        for (int i = clouds.size() - 1; i >= 0; i--) {
            Rect cloud = clouds.get(i);
            if (cloud.right < 0) {
                Log.i("PUPPYSITTER", "Cloud deleted");
                clouds.remove(i);
            }
        }
    }

    public void drawScene(Canvas canvas) {
        //super.onDraw(canvas);

        Rect bounds = new Rect(0,0,getRight(), getBottom());

        // Draw Sky
        canvas.drawRect(bounds, skyPaint);

        // Draw grass
        canvas.drawRect(0,(int)(getHeight() * 0.75), getRight(), getBottom(), grassPaint);

        // Draw picket fence
        // TODO Add triangles to the top of the pickets
        float plankTop = getHeight() * 0.5f;
        float plankBottom = getHeight() * 0.75f;
        float numPickets = 8;
        float picketGapPercentOfPicketWidth = 0.3f;
        float picketWidth = getWidth() / (numPickets * (1 + picketGapPercentOfPicketWidth));
        float gapWidth = picketWidth * picketGapPercentOfPicketWidth;

        // Vertical planks
        float x = gapWidth / 2.0f;
        for (int i = 0; i < numPickets; i++) {
            canvas.drawRect(x, (int)plankTop, x + picketWidth, (int)plankBottom, fencePaint);
            x += picketWidth + gapWidth;
        }
        // Horizontal planks
        canvas.drawRect(0, (int)(plankTop + ((plankBottom - plankTop) * 0.10)), getRight(), (int)(((plankBottom - plankTop) * 0.10) + 50 + plankTop), fencePaint);
        canvas.drawRect(0, (int)(plankTop + ((plankBottom - plankTop) * 0.80)), getRight(), (int)(((plankBottom - plankTop) * 0.80) + 50 + plankTop), fencePaint);

        // Draw clouds
        for (Rect cloud : clouds) {
            canvas.drawRect(cloud, cloudPaint);
        }

        // Draw pets name
        canvas.drawText(pet.getPetName(), 20, getHeight() * 0.35f, petNamePaint);

        // Draw hunger
        Pet.HungerStat hunger = pet.getHungerStatus();
        switch (hunger) {
            case STARVING:
                canvas.drawText("STARVING", 20, getHeight() * 0.40f, petNamePaint);
                break;
            case HUNGRY:
                canvas.drawText("HUNGRY", 20, getHeight() * 0.40f, petNamePaint);
                break;
            case SATISFIED:
                canvas.drawText("SATISFIED", 20, getHeight() * 0.40f, petNamePaint);
                break;
            case FULL:
                canvas.drawText("FULL", 20, getHeight() * 0.40f, petNamePaint);
                break;
        }

        // Draw lonely
        Pet.LonelyStat lonely = pet.getLonelyStatus();
        switch (lonely) {
            case ABANDONED:
                canvas.drawText("ABANDONED", 20, getHeight() * 0.45f, petNamePaint);
                break;
            case LONELY:
                canvas.drawText("LONELY", 20, getHeight() * 0.45f, petNamePaint);
                break;
            case SATISFIED:
                canvas.drawText("SATISFIED", 20, getHeight() * 0.45f, petNamePaint);
                break;
            case FULL:
                canvas.drawText("FULL", 20, getHeight() * 0.45f, petNamePaint);
                break;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.i("PUPPYSITTER", "SURFACE CREATED");
        drawThread = new DrawThread(this);
        drawThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        drawThread.interrupt();

        try {
            drawThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Log.i("PUPPYSITTER", "SURFACE DESTROYED");
    }
}
