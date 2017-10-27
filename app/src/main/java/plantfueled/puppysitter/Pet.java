package plantfueled.puppysitter;

public class Pet {

    private static int petCounter = 0;   // Static ID increments with every new pet created
    private int petID = 0;
    private String petName = "noName"; // name of pet
    private int hungerLevel = 100;
    private int lonelyLevel = 100;

    public Pet(String name){
        petName = name;
        petCounter++;
        petID = petCounter;
    }

    //*****GETTERS & SETTERS*****//
    public int getPetID() {return petID;}
    public String getPetName() {return petName;}
    public void setPetName(String petName) {this.petName = petName;}
    public int getHungerLevel() {return hungerLevel;}
    public void setHungerLevel(int hungerLevel) {this.hungerLevel = hungerLevel;}
    public int getLonelyLevel() {return lonelyLevel;}
    public void setLonelyLevel(int lonelyLevel) {this.lonelyLevel = lonelyLevel;}
}
