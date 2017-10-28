package plantfueled.puppysitter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class PetNotification {

    private Pet.HungerStat lastHungerStat;
    private Pet.LonelyStat lastLonelyStat;

    // Notification stuff
    private Context context;
    private int duration = Toast.LENGTH_SHORT;

    PetNotification(Context context){
        this.context = context;
    }

    public void hungerChange(Pet.HungerStat stat,  String petName){

        CharSequence text = "";
        switch (stat){

            case STARVING:
                text = petName + " is starving";
                break;
            case HUNGRY:
                text = petName + " is hungry";
                break;
            case SATISFIED:
                text = petName + " is satisfied with its hunger";
                break;
            case FULL:
                text = petName + " is full and can't eat anymore";
                break;
        }
        if(text != "")
            toast(text);

    };

    public void lonelyChange(Pet.LonelyStat stat, String petName){
        CharSequence text = "";
        switch (stat){

            case ABANDONED:
                text = petName + " feels abandoned";
                break;
            case LONELY:
                text = petName + " is lonely";
                break;
            case SATISFIED:
                text = petName + " is happy";
                break;
            case FULL:
                text = petName + " socialized as much as it could";
                break;
        }
        if(text != "")
            toast(text);
    };

    private void toast(CharSequence textIn){

        final CharSequence text = textIn;

        // This ensures that the Toast runs on the UI thread since UI elements can only run on the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, text, duration).show();
            }
        });
    }

}
