package plantfueled.puppysitter;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Simon on 10/29/2017.
 */

public class SharedPreferenceHelper {
    private static final String PREFERENCES_NAME_STR = "PetPrefs";
    private static final String PET_NAME_STR = "PetName";
    private static final String PET_HUNGER_STR = "PetHunger";
    private static final String PET_LONELY_STR = "PetLonely";
    private static final String PET_SAVE_TIME_STR = "PetSaveTime";

    private static final String PET_EXISTS_STR = "PetExists";

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME_STR, Context.MODE_PRIVATE);
    }

    public void savePet(Pet pet) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(PET_NAME_STR, pet.getPetName());
        editor.putFloat(PET_HUNGER_STR, pet.getHungerLevel());
        editor.putFloat(PET_LONELY_STR, pet.getLonelyLevel());
        editor.putLong(PET_SAVE_TIME_STR, System.currentTimeMillis());
        editor.putBoolean(PET_EXISTS_STR, true);
        editor.commit();
    }

    public Pet loadPet() {
        String petName = sharedPreferences.getString(PET_NAME_STR, "Invalid");
        float petHunger = sharedPreferences.getFloat(PET_HUNGER_STR, 50);
        float petLonely = sharedPreferences.getFloat(PET_LONELY_STR, 100);
        long lastSaveTime = sharedPreferences.getLong(PET_SAVE_TIME_STR, System.currentTimeMillis());

        Pet pet = new Pet(petName, context, petHunger, petLonely);

        // TODO update pet hunger and lonely to reflect the time that has passed since the pet was last saved

        return pet;
    }

    public boolean petExists() {
        return sharedPreferences.getBoolean(PET_EXISTS_STR, false);
    }

}
