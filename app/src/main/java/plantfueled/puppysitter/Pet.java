package plantfueled.puppysitter;

import java.util.Calendar;
import java.util.Date;

public class Pet {

    private static int petCounter = 0;   // Static ID increments with every new pet created
    private int petID = 0;
    private String petName = "noName"; // name of pet
    private float hungerLevel = 100;
    private float lonelyLevel = 100;
    private Date lastPetUpdate;

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
    public Pet(String name){
        petName = name;
        petCounter++;
        petID = petCounter;
        lastPetUpdate = Calendar.getInstance().getTime();
    }

    // Lower stats with time passed
    public void updatePetStats(){
        final int hungerRate = 10; // points per minute lost
        final int lonelyRate = 15; // points per minute lost
        int minutesPassed = (int)(Calendar.getInstance().getTime().getTime() - lastPetUpdate.getTime())*1000*60;

        // update the stats according to how many minutes have passed
        if(minutesPassed > hungerRate || minutesPassed > lonelyRate){
            hungerLevel = Math.max(0, hungerLevel - minutesPassed/hungerRate);
            lonelyLevel = Math.max(0, lonelyLevel - minutesPassed/lonelyRate);
            lastPetUpdate = Calendar.getInstance().getTime();
        }

    }

    // Reduce hunger (by increasing the value) when pet is fed to a max value of 100
    public void feed(int hungerRemoved){
        hungerLevel = Math.min(hungerLevel+hungerRemoved,100);
    }
    public void feed(){feed(30);} // Default value for feed() param

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

    //*****GETTERS & SETTERS*****//
    public int getPetID() {return petID;}
    public String getPetName() {return petName;}
    public void setPetName(String petName) {this.petName = petName;}
    public float getHungerLevel() {return hungerLevel;}
    public float getLonelyLevel() {return lonelyLevel;}
}
