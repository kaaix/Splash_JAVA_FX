package Modeles.items.weapons;

import Modeles.items.Item;

public class Weapon extends Item {

    protected int firePower;
    protected float fireRate;

    public Weapon(String displayName, String description, int firePower, float fireRate) {
        super(displayName, description);
        this.firePower = firePower;
        this.fireRate = fireRate;
    }

    public int getFirePower() {
        return this.firePower;
    }

    public float getFireRate() {
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
}
