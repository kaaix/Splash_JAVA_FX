package Modeles.items.weapons;

import Modeles.items.Item;

public class Weapon extends Item {

    protected int firePower;
    protected double fireRate;
    private int portee;

    public Weapon(String displayName, String description, int firePower, double fireRate,int portee) {
        super(displayName, description);
        this.firePower = firePower;
        this.fireRate = fireRate;
        this.portee = portee;
    }

    public int getFirePower() {
        return this.firePower;
    }

    public double getFireRate() {
        return this.fireRate;
    }
    
    public static Weapon parseFromString(String weaponStr) {
        switch(weaponStr) {
            case "Shooter": return new Shooter();
            case "Roller": return new Roller();
            case "Charger": return new Charger();
            default: return null;
        }
    }

    public int getPortee() {
        return this.portee;
    }

}
