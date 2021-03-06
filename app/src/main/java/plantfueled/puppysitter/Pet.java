package plantfueled.puppysitter;

import android.app.Activity;
import android.content.Context;

import java.util.Calendar;
import java.util.Date;

public class Pet {

    private String petName = "noName"; // name of pet

    private float hungerLevel = 90;
    private float lonelyLevel = 90;
    private float temperatureLevel = 22;
    private boolean isHidden = false;

    private Date lastPetUpdate;
    private Date lastPetTempUpdate;
    private PetNotification petNotification;
    private PetStatusUI petStatusUI;

    private  HungerStat lastHungerStat;
    private  LonelyStat lastLonelyStat;
    private  TemperatureStat lastTempStat;

    private int points = 0;

    public static final float HUNGER_RATE = 10f; // points per minute lost
    public static final float LONELY_RATE = 15; // points per minute lost

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

    public enum TemperatureStat{
        COLD(15),
        GOOD(22),
        HOT(25);

        private final int level;
        TemperatureStat(int threshHold){
            this.level = threshHold;
        }
    }

    /// Constructor
    public Pet(String name, Context context){
        petName = name;
        lastPetUpdate = Calendar.getInstance().getTime();
        lastPetTempUpdate = Calendar.getInstance().getTime();
        petNotification = new PetNotification(context);
        petStatusUI = new PetStatusUI(context,getHungerStatus(),getLonelyStatus(),getTemperatureStatus(),name);

        // Set current states
        lastHungerStat = getHungerStatus();
        lastLonelyStat = getLonelyStatus();
        lastTempStat = getTemperatureStatus();
        addPoints(0); // Just to force a UI update with current points amount displayed
    }

    public Pet(String name, Context context, float hungerLevel, float lonelyLevel){
        this(name, context);
        this.hungerLevel = hungerLevel;
        this.lonelyLevel = lonelyLevel;
    }

    // Restore pet saved state from memento
    public Pet(Context context, PetMemento memento){
        this(memento.getPetName(),context);

        hungerLevel = memento.getHungerLevel();
        lonelyLevel = memento.getLonelyLevel();
        temperatureLevel = memento.getTemperatureLevel();

        // Set current states
        lastHungerStat = getHungerStatus();
        lastLonelyStat = getLonelyStatus();
        lastTempStat = getTemperatureStatus();
        points = memento.getPoints();
        addPoints(0); // Just to force a UI update with current points amount displayed

        lastPetTempUpdate = memento.getLastPetTempUpdate();
        lastPetUpdate = memento.getLastPetUpdate();

        checkStatusChange();
    };

    public PetMemento saveState(){
        return new PetMemento(petName,hungerLevel,lonelyLevel,temperatureLevel,lastPetUpdate,lastPetTempUpdate,points);
    }

    // Lower stats with time passed
    public void updatePetStats(){
        int minutesPassed = (int)(Calendar.getInstance().getTime().getTime() - lastPetUpdate.getTime())/1000/60;

        // update the stats according to how many minutes have passed when visible
        if(minutesPassed >= 1 && !isHidden){

            hungerLevel = Math.max(0, hungerLevel - minutesPassed/HUNGER_RATE);
            lonelyLevel = Math.max(0, lonelyLevel - minutesPassed/LONELY_RATE);
            lastPetUpdate = Calendar.getInstance().getTime();

            checkStatusChange();
        }

    }

    // Reduce hunger (by increasing the value) when pet is fed to a max value of 100
    public boolean feed(int hungerRemoved){
        if(getHungerStatus() == HungerStat.FULL){
            petNotification.notHungry(petName);
            return false;
        }
        else{
            hungerLevel = Math.min(hungerLevel+hungerRemoved,HungerStat.FULL.level);
            addPoints(6);
            checkStatusChange();
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
            lonelyLevel = Math.min(lonelyLevel+lonelyAdded, LonelyStat.FULL.level);
            addPoints(1);
            checkStatusChange();
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

    public TemperatureStat getTemperatureStatus(){
        if(temperatureLevel < TemperatureStat.COLD.level)
            return TemperatureStat.COLD;
        else if(temperatureLevel > TemperatureStat.HOT.level)
            return TemperatureStat.HOT;
        else
            return TemperatureStat.GOOD;
    }

    public void checkStatusChange(){
        checkHungerChange();
        checkLonelinessChange();
        checkTempChange();
    }

    private void checkHungerChange(){
        // Check for Hunger Status Change
        if(lastHungerStat != getHungerStatus()){
            lastHungerStat = getHungerStatus();
            petNotification.hungerChange(lastHungerStat, petName);
            petStatusUI.hungerChange(lastHungerStat);
            lastHungerStat = getHungerStatus();
        }
    }

    private void checkLonelinessChange(){
        // Check for Loneliness Status Change
        if(lastLonelyStat != getLonelyStatus()){
            lastLonelyStat = getLonelyStatus();
            petNotification.lonelyChange(lastLonelyStat, petName);
            petStatusUI.lonelyChange(lastLonelyStat);
            lastLonelyStat = getLonelyStatus();
        }
    }

    private void checkTempChange(){

        // Award points if enough time has passed in good status
        checkHowLongPetComfortable();

        // If pet isn't comfortable, don't accumulate time to award points
        if(getTemperatureStatus() != TemperatureStat.GOOD)
            lastPetTempUpdate = Calendar.getInstance().getTime();

        // Check for Temperature Status Change
        if(lastTempStat != getTemperatureStatus()){
            lastTempStat = getTemperatureStatus();
            petNotification.temperatureChange(lastTempStat, petName);
            petStatusUI.temperatureChange(lastTempStat);
            lastTempStat = getTemperatureStatus(); // TODO IS THERE ANY REASON I SET THIS TWICE??
        }
    }

    private void checkHowLongPetComfortable(){
        int minutesPassed = (int)(Calendar.getInstance().getTime().getTime() - lastPetTempUpdate.getTime())/1000/60;

        // give points for every hour spent in "Good" temperature conditions
        // Note: Only checks last time app was open and now
        // doesn't know if pet was uncomfortable when app was closed...
        if(minutesPassed > 60){
            int pointsAwarded = minutesPassed/60;
            addPoints(pointsAwarded);
            lastPetTempUpdate = Calendar.getInstance().getTime();
        }
    }

    private void addPoints(int amountAdded){
        points += amountAdded;
        petStatusUI.setPoints(points);
    }

    public void hide(){
        if(!isHidden){
            petStatusUI.hideUI();
            petNotification.hide();
            isHidden = true;
        }
    }

    public void show(){
        if(isHidden){
            petStatusUI.showUI(getHungerStatus(),getLonelyStatus(),getTemperatureStatus());
            isHidden = false;
        }
    }

    //*****GETTERS & SETTERS*****//
    public String getPetName() {return petName;}
    public void setPetName(String petName) {this.petName = petName;}
    public PetNotification getNotification() {return petNotification;}

    public void setTemperatureLevel(float currTemp) {
        temperatureLevel = currTemp;
        checkTempChange();
    }

    // ONLY FOR DEBUGGING
    public void setHungerLonelyTempLevel(float hungerLvl, float lonelyLvl, float temp) {

        if(hungerLvl >= 0 && hungerLvl <= HungerStat.FULL.level)
            hungerLevel = hungerLvl;

        if(lonelyLvl >= 0 && lonelyLvl <= LonelyStat.FULL.level)
            lonelyLevel = lonelyLvl;

        temperatureLevel = temp;

        checkStatusChange();
    }

}
