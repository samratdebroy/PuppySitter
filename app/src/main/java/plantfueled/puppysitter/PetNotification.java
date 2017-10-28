package plantfueled.puppysitter;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

public class PetNotification {

    private Pet pet;
    private Pet.HungerStat lastHungerStat;
    private Pet.LonelyStat lastLonelyStat;

    // Notification stuff
    private Context context;
    int duration = Toast.LENGTH_SHORT;

    PetNotification(Context context){
        this.context = context;
    }

    public void setPet(Pet pet) {
        this.pet = pet;
        lastHungerStat = pet.getHungerStatus();
        lastLonelyStat = pet.getLonelyStatus();
    }

    public void checkStatusChange(){

        // Check for Hunger Status Change
        if(lastHungerStat != pet.getHungerStatus()){
            lastHungerStat = pet.getHungerStatus();
            hungerChange(lastHungerStat);
        }

        // Check for Loneliness Status Change
        if(lastLonelyStat != pet.getLonelyStatus()){
            lastLonelyStat = pet.getLonelyStatus();
            lonelyChange(lastLonelyStat);
        }
    }

    private void hungerChange(Pet.HungerStat stat){

        CharSequence text = "";
        switch (stat){

            case STARVING:
                text = pet.getPetName() + " is starving";
                break;
            case HUNGRY:
                text = pet.getPetName() + " is hungry";
                break;
            case SATISFIED:
                text = pet.getPetName() + " is satisfied with its hunger";
                break;
            case FULL:
                text = pet.getPetName() + " is full and can't eat anymore";
                break;
        }
        if(text != "")
            toast(text);

    };

    private void lonelyChange(Pet.LonelyStat stat){
        CharSequence text = "";
        switch (stat){

            case ABANDONED:
                text = pet.getPetName() + " feels abandoned";
                break;
            case LONELY:
                text = pet.getPetName() + " is lonely";
                break;
            case SATISFIED:
                text = pet.getPetName() + " is happy";
                break;
            case FULL:
                text = pet.getPetName() + " socialized as much as it could";
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
