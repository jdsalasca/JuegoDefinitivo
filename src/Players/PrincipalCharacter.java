package src.Players;
// import java.util.ArrayList;

public class PrincipalCharacter {  
    private String name;
    private int life;
    private int strong;
    // private ArrayList objets;
    // private ArrayList Body;
    private int defense;
    private int agility;
    private int intelect;
    private int wisdom;
    private int progress;
    // private ArrayList Inventary;
    // private ArrayList equipedObjects;
    // private ArrayList inventory;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getLife() {
        return this.life;
    }
    public void setLife(int life) {
        this.life = life;
    }
    public int getStrong() {
        return this.strong;
    }
    public void setStrong(int strong) {
        this.strong = strong;
    }
    public int getDefense() {
        return this.defense;
    }
    public void setDefense(int defense) {
        this.defense = defense;
    }
    public int getAgility() {
        return this.agility;
    }
    public void setAgility(int Agility) {
        this.agility = Agility;
    }
    public int getIntelect() {
        return this.intelect;
    }
    public void setIntelect(int intelect) {
        this.intelect = intelect;
    }
    public int getWisdom() {
        return this.wisdom;
    }
    public void setWisdom(int wisdom) {
        
        this.wisdom = wisdom;
    }
    public int getProgress() {
        return this.progress;
    }
    public void setProgress(int progress) {
        
        this.progress = progress;
    }
    public PrincipalCharacter() {
        this.name = "generic";
        this.life = 100;
        this.strong = 10;
        this.defense = 20;
        this.agility = 10;
        this.intelect = 0;
        this.wisdom = 0;
        this.progress = 0;
    }
    @Override
    public String toString() {
        
        return "name " + this.name + "\nlife " + this.life + "\nstrong " + this.strong + "\ndefense " + this.defense + "\nagility " + this.agility + "\nintelect " + this.intelect + "\nwisdom " + this.wisdom + "\nprogress " + this.progress;
        // return this.name + "\n" + this.life + "\n" +  this.strong + "\n" +  this.defense + "\n" +  this.agility + "\n" +  this.intelect + "\n" +  this.wisdom;
}
}