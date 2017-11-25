package plantfueled.puppysitter;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

public class PetNotification {

    private static final int NOTIFICATION_UNIQUE_ID = "PET_SITTER_NOTIFICATION_ID_plantfueled.puppysitter".hashCode();

    // Notification stuff
    private Context context;
    private int duration = Toast.LENGTH_SHORT;
    private Toast myToast;

    private NotificationManager notificationManager;

    PetNotification(Context context){
        this.context = context;
        myToast = Toast.makeText(context, null, duration);

        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public void notHungry(String petName){
        CharSequence text = petName + " isn't hungry right now";
        toast(text);
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
        if(text != "") {
            toast(text);
            notify(text);
        }

    }

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
        if(text != "") {
            toast(text);
            notify(text);
        }
    }

    public void temperatureChange(Pet.TemperatureStat stat, String petName){
        CharSequence text = "";
        switch (stat){

            case COLD:
                text = petName + " feels cold";
                break;
            case GOOD:
                text = petName + " likes the temperature here";
                break;
            case HOT:
                text = petName + " feels hot";
                break;
        }
        if(text != "") {
            toast(text);
            notify(text);
        }
    }

    public void hide(){
        toast("You need to move closer to your pet");
    }

    private void toast(CharSequence textIn){

        final CharSequence text = textIn;

        // This ensures that the Toast runs on the UI thread since UI elements can only run on the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                myToast.setText(text);
                myToast.show();
            }
        });
    }

    private void notify(CharSequence text) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context).setSmallIcon(R.drawable.puppy).setContentTitle("Puppy Alert").setContentText(text);
        notificationManager.notify(NOTIFICATION_UNIQUE_ID, builder.build());
    }

}
