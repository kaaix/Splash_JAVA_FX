package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;

public class HealthBoost extends Consumable {
    
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 30;

    private int healthBoostValue;

    public HealthBoost(int healthBoostValue) {
        super("Health boost", String.format("Boosts user's health by %d hp.", healthBoostValue));
        this.healthBoostValue = healthBoostValue;
    }

    public HealthBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }
    
        @Override
    public void useOn(Character character) {
        character.addHealth(healthBoostValue);
    }
    
}
