package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class SpeedBoost extends Consumable {
    
    private static final int MIN_VALUE = 3;
    private static final int MAX_VALUE = 10;
    
    private int speedBoostValue;

    public SpeedBoost(int speedBoostValue) {
        super(
                I18N.get("bonus.speed.name"),
                I18N.getFormatted("bonus.speed.desc", speedBoostValue)
        );
        this.speedBoostValue = speedBoostValue;
    }


    public SpeedBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    @Override
    public void useOn(Character character) {
        character.addSpeed(this.speedBoostValue);
        this.markAsApplied(); // ← n’oublie pas ça
    }

    @Override
    public String toString() {
        return "SpeedBoost:" + speedBoostValue;
    }


}

