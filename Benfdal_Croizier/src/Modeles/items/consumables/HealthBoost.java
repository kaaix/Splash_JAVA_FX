package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class HealthBoost extends Consumable {
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 30;

    private int healthBoostValue;
    private boolean alreadyApplied = false;

    public HealthBoost(int healthBoostValue) {
        super(
                I18N.get("bonus.health.name"),
                I18N.getFormatted("bonus.health.desc", healthBoostValue)
        );
        this.healthBoostValue = healthBoostValue;
    }


    public HealthBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    @Override
    public void useOn(Character character) {
        if (!alreadyApplied) {
            character.addMaxHealth(healthBoostValue);
            character.addHealth(healthBoostValue); // soin initial
            alreadyApplied = true;
        }
        // Pas de soin répété après
    }

}

