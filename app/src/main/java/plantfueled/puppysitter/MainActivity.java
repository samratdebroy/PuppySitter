package plantfueled.puppysitter;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private PetSurface petSurface;

    private Pet pet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load pet
        pet = new Pet("Doggo the Debug Dog");

        petSurface = (PetSurface) findViewById(R.id.main_pet_view);
        petSurface.setPet(pet);

    }
}
