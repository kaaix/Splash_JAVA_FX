package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;

public class SpeedBoost extends Consumable {
    
    private static final int MIN_VALUE = 3;
    private static final int MAX_VALUE = 10;
    
    private int speedBoostValue;

    public SpeedBoost(int speedBoostValue) {
        super("Attack power boost", String.format("Boosts user's speed by %d points.", speedBoostValue));
        this.speedBoostValue = speedBoostValue;
    }

    public SpeedBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }
    
        @Override
    public void useOn(Character character) {
        character.addSpeed(this.speedBoostValue);
    }
    
}

