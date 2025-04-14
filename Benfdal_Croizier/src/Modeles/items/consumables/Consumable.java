package Modeles.items.consumables;

import Modeles.items.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Modeles.characters.Character;

public abstract class Consumable extends Item {
    
    public Consumable(String displayName, String description) {
        super(displayName, description);
    }

    public abstract void useOn(Character character);

    public static Consumable getRandomConsumable() {
        List<Consumable> possibility = new ArrayList<Consumable>();
        Collections.addAll(possibility, 
            new AttackPowerBoost(), 
            new HealthBoost(),
            new LuckBoost(),
            new SpeedBoost());
        return possibility.get(new Random().nextInt(possibility.size()));
    }

    public static List<Consumable> getRandomConsumable(int n) {
        List<Consumable> res = new ArrayList<Consumable>();
        for(int i = 0 ; i < n ; i++) {
            res.add(getRandomConsumable());
        }
        return res;
    }

}
