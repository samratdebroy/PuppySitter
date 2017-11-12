package plantfueled.puppysitter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import plantfueled.puppysitter.bluetooth.BluetoothActivity;

public class MainActivity extends BluetoothActivity {

    private PetSurface petSurface;
    private Pet pet;

    private Context appContext;

    public final String TAG = "MainActivity";
    private static final int PERMISSION_FINE_LOCATION = 1;

    protected ImageButton feedButton = null;
    protected ImageView bonetoFeed = null;
    protected Animation boneFeedAnimation = null;

    private SharedPreferenceHelper sharedPrefHelper;

    private ImageView dogImage;
    private Animation dogHopAnimation;

    private boolean isFar = false;

    /// SET THIS TO TRUE IF YOU WANT ACTIONBAR TO DEBUG ///
    private final boolean isDebugMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appContext = getApplicationContext();
        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_FINE_LOCATION);

        // Load pet
        sharedPrefHelper = new SharedPreferenceHelper(MainActivity.this);
        if(sharedPrefHelper.petExists())
            pet = sharedPrefHelper.loadPet();
        else
            pet = new Pet("Doggo the Debug Dog",MainActivity.this);

        petSurface = (PetSurface) findViewById(R.id.main_pet_view);
        petSurface.setPet(pet);
        setupUI();
        setIsFar(); // set to far initially

        bluetoothInit();

        if(!isDebugMode){
            getSupportActionBar().hide();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {


                } else {

                   Toast.makeText(appContext, "You need to allow location permissions", Toast.LENGTH_SHORT);
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.isFarButton:
                setIsFar();
                return true;
            case R.id.isNearButton:
                setIsNear();
                return true;
            case R.id.StarveButton:
                pet.setHungerLonelyTempLevel(1,-1,22);
                return true;
            case R.id.AbandonButton:
                pet.setHungerLonelyTempLevel(-1,1,22);
                return true;
            case R.id.FreezeButton:
                pet.setHungerLonelyTempLevel(-1,-1,-22);
                return true;
            case R.id.BurnButton:
                pet.setHungerLonelyTempLevel(-1,-1,222);
                return true;
            case R.id.LoveButton:
                onSoundReceived();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void onBluetoothSuccess() {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                // TODO what to do on success
            }
        });
    }

    @Override
    public void onBluetoothFailure() {
        // TODO what to do on failure
    }

    @Override
    public void setIsNear(){
        // If user is close to pet, show everything
        if(isFar){
            feedButton.setVisibility(View.VISIBLE);
            feedButton.setEnabled(true);
            dogImage.setVisibility(View.VISIBLE);
            pet.show();
            isFar = false;
        }
    };

    @Override
    public void setIsFar(){
        // If user is too far from pet, hide everything
        if(!isFar){
            feedButton.setVisibility(View.INVISIBLE);
            feedButton.setEnabled(false);
            dogImage.setVisibility(View.INVISIBLE);
            pet.hide();
            isFar = true;
        }
    };

    @Override
    public void onSoundReceived() {
        Log.i("WHAT IS LOVE", "BABY DON'T HURT ME");
        if(pet.love()){
            // Save pet state after it changed
            // TODO Maybe move saves to app exits only so it's not repreatedly called
            sharedPrefHelper.savePet(pet);
        }

    }
}
