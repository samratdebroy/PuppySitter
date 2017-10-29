package plantfueled.puppysitter;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    protected static final String TAG = "Main Activity";

    private PetSurface petSurface;
    private Pet pet;

    protected ImageButton feedButton = null;
    protected ImageView bonetoFeed = null;
    protected Animation boneFeedAnimation = null;

    private SharedPreferenceHelper sharedPrefHelper;

    private ImageView dogImage;
    private Animation dogHopAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load pet
        sharedPrefHelper = new SharedPreferenceHelper(MainActivity.this);
        if(sharedPrefHelper.petExists())
            pet = sharedPrefHelper.loadPet();
        else
            pet = new Pet("Doggo the Debug Dog",MainActivity.this);

        petSurface = (PetSurface) findViewById(R.id.main_pet_view);
        petSurface.setPet(pet);
        setupUI();
    }

    protected void setupUI()
    {
        feedButton = (ImageButton) findViewById(R.id.feedButton);
        feedButton.setOnClickListener(onClickFeedButton);

        bonetoFeed = (ImageView)  findViewById(R.id.boneImageView);
        boneFeedAnimation = AnimationUtils.loadAnimation(this, R.anim.feedbone);

        dogImage = (ImageView) findViewById(R.id.img_puppy);
        dogHopAnimation = AnimationUtils.loadAnimation(this, R.anim.dog_hop);
    }

    private ImageButton.OnClickListener onClickFeedButton = new ImageButton.OnClickListener(){
        public void onClick(View v){
            Log.d(TAG,"The onClick() feedButton Event");

            // Feeds the pet if the animation isn't already running and the pet can eat
            if(dogHopAnimation.hasEnded() || !dogHopAnimation.hasStarted()){
                if(pet.feed()){
                    bonetoFeed.setVisibility(View.VISIBLE);
                    bonetoFeed.startAnimation(boneFeedAnimation);
                    bonetoFeed.setVisibility(View.INVISIBLE);
                    dogImage.startAnimation(dogHopAnimation);
                    sharedPrefHelper.savePet(pet);
                }
            }
        }
    };

}
