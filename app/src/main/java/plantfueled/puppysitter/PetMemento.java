package plantfueled.puppysitter;

import java.util.Date;

public class PetMemento {

    public String getPetName() {
        return petName;
    }

    public float getHungerLevel() {
        return hungerLevel;
    }

    public float getLonelyLevel() {
        return lonelyLevel;
    }

    public float getTemperatureLevel() {
        return temperatureLevel;
    }

    public Date getLastPetUpdate() {
        return lastPetUpdate;
    }

    public Date getLastPetTempUpdate() {
        return lastPetTempUpdate;
    }

    public int getPoints() {
        return points;
    }

    private final String petName;
    private float hungerLevel = 90;
    private float lonelyLevel = 90;
    private float temperatureLevel = 22;

    private Date lastPetUpdate;
    private Date lastPetTempUpdate;

    private int points = 0;


    PetMemento(String petName, float hungerLevel, float lonelyLevel, float temperatureLevel
    , Date lastPetUpdate, Date lastPetTempUpdate,int points){
        this.petName = petName;
        this.hungerLevel = hungerLevel;
        this.lonelyLevel = lonelyLevel;
        this.temperatureLevel = temperatureLevel;
        this.lastPetUpdate = lastPetUpdate;
        this.lastPetTempUpdate = lastPetTempUpdate;
        this.points = points;
    };

    // SET THIS IN CONSTRUCTOR of PET
    private Pet.HungerStat lastHungerStat;
    private Pet.LonelyStat lastLonelyStat;
    private Pet.TemperatureStat lastTempStat;



}
