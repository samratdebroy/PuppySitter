package plantfueled.puppysitter;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class Pet {

    private static int petCounter = 0;   // Static ID increments with every new pet created
    private int petID = 0;
    private String petName = "noName"; // name of pet
    private float hungerLevel = 50;
    private float lonelyLevel = 100;
    private Date lastPetUpdate;
    private PetNotification petNotification;

    final float HUNGER_RATE = 10f; // points per minute lost
    final float LONELY_RATE = 15; // points per minute lost

    public enum HungerStat{
        STARVING(5),
        HUNGRY(50),
        SATISFIED(99),
        FULL(100);

        private final int level;
        HungerStat(int threshHold){
            this.level = threshHold;
        }
    }

    public enum LonelyStat{
        ABANDONED(5),
        LONELY(50),
        SATISFIED(99),
        FULL(100);

        private final int level;
        LonelyStat(int threshHold){
            this.level = threshHold;
        }
    }

    /// Constructor
    public Pet(String name, Context context){
        petName = name;
        petCounter++;
        petID = petCounter;
        lastPetUpdate = Calendar.getInstance().getTime();
        petNotification = new PetNotification(context);
    }

    public Pet(String name, Context context, float hungerLevel, float lonelyLevel){
        this(name, context);
        this.hungerLevel = hungerLevel;
        this.lonelyLevel = lonelyLevel;
    }

    // Lower stats with time passed
    public void updatePetStats(){
        int minutesPassed = (int)(Calendar.getInstance().getTime().getTime() - lastPetUpdate.getTime())/1000/60;

        // update the stats according to how many minutes have passed
        if(minutesPassed >= 1){

            // Cache current Pet Stats
            HungerStat lastHungerStat = getHungerStatus();
            LonelyStat lastLonelyStat = getLonelyStatus();

            hungerLevel = Math.max(0, hungerLevel - minutesPassed/HUNGER_RATE);
            lonelyLevel = Math.max(0, lonelyLevel - minutesPassed/LONELY_RATE);
            lastPetUpdate = Calendar.getInstance().getTime();

            checkStatusChange(lastHungerStat,lastLonelyStat);
        }

    }

    // Reduce hunger (by increasing the value) when pet is fed to a max value of 100
    public boolean feed(int hungerRemoved){
        if(getHungerStatus() == HungerStat.FULL){
            petNotification.notHungry(petName);
            return false;
        }
        else{
            hungerLevel = Math.min(hungerLevel+hungerRemoved,100);
            petNotification.hungerChange(getHungerStatus(), petName);
            return true;
        }
    }
    public boolean feed(){return  feed(30);} // Default value for feed() param

    public HungerStat getHungerStatus(){
        if(hungerLevel < HungerStat.STARVING.level)
            return HungerStat.STARVING;
        else if(hungerLevel < HungerStat.HUNGRY.level)
            return HungerStat.HUNGRY;
        else if(hungerLevel < HungerStat.SATISFIED.level)
            return HungerStat.SATISFIED;
        else
            return HungerStat.FULL;
    }

    public boolean love() { return love(5); }

    public boolean love(int lonelyAdded) {
        if(getLonelyStatus() == LonelyStat.FULL){
            return false;
        }
        else{
            //hungerLevel = Math.min(hungerLevel+hungerRemoved,100);
            lonelyLevel = Math.min(lonelyLevel+lonelyAdded, LonelyStat.FULL.level);
            petNotification.lonelyChange(getLonelyStatus(), petName);
            return true;
        }
    }

    public LonelyStat getLonelyStatus(){
        if(lonelyLevel < LonelyStat.ABANDONED.level)
            return LonelyStat.ABANDONED;
        else if(lonelyLevel < LonelyStat.LONELY.level)
            return LonelyStat.LONELY;
        else if(lonelyLevel < LonelyStat.SATISFIED.level)
            return LonelyStat.SATISFIED;
        else
            return LonelyStat.FULL;
    }

    public void checkStatusChange(HungerStat lastHungerStat, LonelyStat lastLonelyStat){

        // Check for Hunger Status Change
        if(lastHungerStat != getHungerStatus()){
            lastHungerStat = getHungerStatus();
            petNotification.hungerChange(lastHungerStat, petName);
        }

        // Check for Loneliness Status Change
        if(lastLonelyStat != getLonelyStatus()){
            lastLonelyStat = getLonelyStatus();
            petNotification.lonelyChange(lastLonelyStat, petName);
        }
    }

    //*****GETTERS & SETTERS*****//
    public int getPetID() {return petID;}
    public String getPetName() {return petName;}
    public void setPetName(String petName) {this.petName = petName;}
    public float getHungerLevel() {return hungerLevel;}
    public float getLonelyLevel() {return lonelyLevel;}
    public PetNotification getNotification() {return petNotification;}
}
