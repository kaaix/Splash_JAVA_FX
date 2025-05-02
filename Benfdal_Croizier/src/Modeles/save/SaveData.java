package Modeles.save;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {
    public int floorLevel;
    public double playerX, playerY;
    public int currentHp;
    public String weapon;
    public List<String> bagItems;
    public int timeSeconds;

    public String nomHero;
    public int maxHp;



    public SaveData(int floor, double x, double y, int hp, String weapon, List<String> bagItems, int timeSeconds, String nomHero, int maxHp) {
        this.floorLevel = floor;
        this.playerX = x;
        this.playerY = y;
        this.currentHp = hp;
        this.weapon = weapon;
        this.bagItems = bagItems;
        this.timeSeconds = timeSeconds;

        this.nomHero = nomHero;
        this.maxHp = maxHp; // ✅ nouvelle ligne
    }

}
