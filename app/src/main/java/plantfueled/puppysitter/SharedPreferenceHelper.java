package plantfueled.puppysitter;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * Created by Simon on 10/29/2017.
 */

public class SharedPreferenceHelper {
    private static final String PREFERENCES_NAME_STR = "PetPrefs";
    private static final String PET_NAME_STR = "PetName";
    private static final String PET_HUNGER_STR = "PetHunger";
    private static final String PET_LONELY_STR = "PetLonely";
    private static final String PET_TEMPERATURE_STR = "PetTemperature";
    private static final String PET_POINTS_STR = "PetPoints";
    private static final String PET_SAVE_TIME_STR = "PetSaveTime";
    private static final String PET_LAST_UPDATE_STR = "PetLastUpdateTime";
    private static final String PET_LAST_TEMP_UPDATE_STR = "PetLastTempUpdateTime";

    private static final String PET_EXISTS_STR = "PetExists";

    private SharedPreferences sharedPreferences;
    private Context context;

    public SharedPreferenceHelper(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(PREFERENCES_NAME_STR, Context.MODE_PRIVATE);
    }

    public void savePet(PetMemento petMemento) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PET_NAME_STR, petMemento.getPetName());

        editor.putFloat(PET_HUNGER_STR, petMemento.getHungerLevel());
        editor.putFloat(PET_LONELY_STR, petMemento.getLonelyLevel());
        editor.putFloat(PET_TEMPERATURE_STR, petMemento.getTemperatureLevel());

        editor.putInt(PET_POINTS_STR, petMemento.getPoints());

        editor.putLong(PET_LAST_UPDATE_STR, petMemento.getLastPetUpdate().getTime());
        editor.putLong(PET_LAST_TEMP_UPDATE_STR, petMemento.getLastPetTempUpdate().getTime());

        editor.putBoolean(PET_EXISTS_STR, true);
        editor.commit();
    }

    public void savePet(Pet pet) {
        savePet(pet.saveState());
    }

    public Pet loadPet() {
        String petName = sharedPreferences.getString(PET_NAME_STR, "Invalid");

        float petHunger = sharedPreferences.getFloat(PET_HUNGER_STR, 80);
        float petLonely = sharedPreferences.getFloat(PET_LONELY_STR, 100);
        float petTemp = sharedPreferences.getFloat(PET_TEMPERATURE_STR, 22);

        int points = sharedPreferences.getInt(PET_POINTS_STR, 0);

        Date lastUpdate = new Date(sharedPreferences.getLong(PET_LAST_UPDATE_STR, System.currentTimeMillis()));
        Date lastTempUpdate = new Date(sharedPreferences.getLong(PET_LAST_TEMP_UPDATE_STR, System.currentTimeMillis()));

        PetMemento memento = new PetMemento(petName,petHunger,petLonely,petTemp,lastUpdate,lastTempUpdate,points);
        Pet pet = new Pet(context, memento);

        return pet;
    }

    public boolean petExists() {
        return sharedPreferences.getBoolean(PET_EXISTS_STR, false);
    }

}
