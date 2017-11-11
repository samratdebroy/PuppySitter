package plantfueled.puppysitter;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.util.List;

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

    // TODO Remove me
    private Button testButton;

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

        testButton = (Button) findViewById(R.id.bt_ble);
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dogImage.startAnimation(dogHopAnimation);
            }
        });

        bluetoothInit();
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
        //btService.btCheck();
        //btService.btCheck();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //btService.scanLeDevice();
        return super.onOptionsItemSelected(item);
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
        testButton.setEnabled(true);
    }

    @Override
    public void onBluetoothFailure() {

    }
}
