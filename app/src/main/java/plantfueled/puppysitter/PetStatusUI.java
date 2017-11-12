package plantfueled.puppysitter;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PetStatusUI{

    private Activity activity;
    private ArrayList<View> viewList;

    private ImageView hungerBox = null;
    private ImageView lonelyBox = null;
    private ImageView temperatureBox = null;

    private ImageView hungerIcon = null;
    private ImageView lonelyIcon = null;
    private ImageView temperatureIcon = null;

    private TextView hungerText = null;
    private TextView lonelyText = null;
    private TextView temperatureText = null;
    private TextView petNameText = null;

    private final int badStatusColor = Color.rgb(255,140,0);

    public PetStatusUI(Context context, Pet.HungerStat hungerStat, Pet.LonelyStat lonelyStat, Pet.TemperatureStat temperatureStat,String petName){

        this.activity = getActivity(context);
        viewList = new ArrayList<View>();

        // Init all UI
        hungerBox = (ImageView)  activity.findViewById(R.id.hungerViewBox);
        viewList.add(hungerBox);
        lonelyBox = (ImageView)  activity.findViewById(R.id.lonelyViewBox);
        viewList.add(lonelyBox);
        temperatureBox = (ImageView)  activity.findViewById(R.id.temperatureViewBox);
        viewList.add(temperatureBox);

        hungerIcon = (ImageView)  activity.findViewById(R.id.hungerStatusIcon);
        viewList.add(hungerIcon);
        lonelyIcon = (ImageView)  activity.findViewById(R.id.lonelyStatusIcon);
        viewList.add(lonelyIcon);
        temperatureIcon = (ImageView)  activity.findViewById(R.id.temperatureStatusIcon);
        viewList.add(temperatureIcon);

        hungerText = (TextView)  activity.findViewById(R.id.hungerStatusText);
        viewList.add(hungerText);
        lonelyText = (TextView)  activity.findViewById(R.id.lonelyStatusText);
        viewList.add(lonelyText);
        temperatureText = (TextView)  activity.findViewById(R.id.temperatureStatusText);
        viewList.add(temperatureText);
        petNameText = (TextView)  activity.findViewById(R.id.petNameText);
        viewList.add(petNameText);

        // Set up UI for current Pet State
        checkStates(hungerStat,lonelyStat,temperatureStat);
        changePetName(petName);
    }

    public void hungerChange(final Pet.HungerStat currStat){
        // Run on MainThread since it's UI
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (currStat) {
                    case STARVING:
                        hungerBox.setBackgroundColor(badStatusColor);
                        hungerText.setTextColor(badStatusColor);
                        hungerText.setText("STARVING");
                        break;
                    case HUNGRY:
                        hungerBox.setBackgroundColor(Color.YELLOW);
                        hungerText.setTextColor(Color.YELLOW);
                        hungerText.setText("HUNGRY");
                        break;
                    case SATISFIED:
                        hungerBox.setBackgroundColor(Color.GREEN);
                        hungerText.setTextColor(Color.GREEN);
                        hungerText.setText("GOOD");
                        break;
                    case FULL:
                        hungerBox.setBackgroundColor(Color.GREEN);
                        hungerText.setTextColor(Color.GREEN);
                        hungerText.setText("FULL");
                        break;
                }
            }
        });
    }

    public void lonelyChange(final Pet.LonelyStat currStat){
        // Run on MainThread since it's UI
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (currStat) {
                    case ABANDONED:
                        lonelyBox.setBackgroundColor(badStatusColor);
                        lonelyText.setTextColor(badStatusColor);
                        lonelyText.setText("ABANDONED");
                        break;
                    case LONELY:
                        lonelyBox.setBackgroundColor(Color.YELLOW);
                        lonelyText.setTextColor(Color.YELLOW);
                        lonelyText.setText("LONELY");
                        break;
                    case SATISFIED:
                        lonelyBox.setBackgroundColor(Color.GREEN);
                        lonelyText.setTextColor(Color.GREEN);
                        lonelyText.setText("HAPPY");
                        break;
                    case FULL:
                        lonelyBox.setBackgroundColor(Color.GREEN);
                        lonelyText.setTextColor(Color.GREEN);
                        lonelyText.setText("FULL");
                        break;
                }
            }
        });
    }

    public void temperatureChange(final Pet.TemperatureStat currStat){
        // Run on MainThread since it's UI
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                switch (currStat) {
                    case COLD:
                        temperatureBox.setBackgroundColor(badStatusColor);
                        temperatureIcon.setImageResource(R.drawable.tempcoldstatusicon);
                        temperatureText.setTextColor(badStatusColor);
                        temperatureText.setText("COLD");
                        break;
                    case GOOD:
                        temperatureBox.setBackgroundColor(Color.GREEN);
                        temperatureText.setTextColor(Color.GREEN);
                        temperatureIcon.setImageResource(R.drawable.tempgoodstatusicon);
                        temperatureText.setText("GOOD");
                        break;
                    case HOT:
                        temperatureBox.setBackgroundColor(badStatusColor);
                        temperatureText.setTextColor(badStatusColor);
                        temperatureIcon.setImageResource(R.drawable.temphotstatusicon);
                        temperatureText.setText("HOT");
                        break;
                }
            }
        });
    }

    public void changePetName(final String petName){
        // Run on MainThread since it's UI
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
               petNameText.setText(petName);
            }
        });
    }

    public void hideUI(){
        for(View view : viewList){
            view.setVisibility(View.INVISIBLE);
        }
    }

    public void showUI(Pet.HungerStat hungerStat, Pet.LonelyStat lonelyStat, Pet.TemperatureStat temperatureStat){
        for(View view : viewList){
            view.setVisibility(View.VISIBLE);
        }
        checkStates(hungerStat,lonelyStat,temperatureStat);
    }

    private void checkStates(Pet.HungerStat hungerStat, Pet.LonelyStat lonelyStat, Pet.TemperatureStat temperatureStat){
        hungerChange(hungerStat);
        lonelyChange(lonelyStat);
        temperatureChange(temperatureStat);
    }

    public Activity getActivity(Context context)
    {
        if (context == null)
        {
            return null;
        }
        else if (context instanceof ContextWrapper)
        {
            if (context instanceof Activity)
            {
                return (Activity) context;
            }
            else
            {
                return getActivity(((ContextWrapper) context).getBaseContext());
            }
        }

        return null;
    }

}
