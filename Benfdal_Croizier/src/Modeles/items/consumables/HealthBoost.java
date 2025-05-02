/**
 * Consumable qui augmente la vie maximale et restaure la vie courante.
 * Le boost appliqué est compris entre MIN_VALUE (10) et MAX_VALUE (30).
 */
package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class HealthBoost extends Consumable {
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 30;

    private int healthBoostValue;
    private boolean alreadyApplied = false;

    /**
     * Construit un HealthBoost avec une valeur précise.
     *
     * @param healthBoostValue valeur du bonus de vie à appliquer
     */
    public HealthBoost(int healthBoostValue) {
        super(
                I18N.get("bonus.health.name"),
                I18N.getFormatted("bonus.health.desc", healthBoostValue)
        );
        this.healthBoostValue = healthBoostValue;
    }

    /**
     * Construit un HealthBoost avec une valeur aléatoire
     * entre MIN_VALUE et MAX_VALUE.
     */
    public HealthBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    /**
     * Applique le bonus de vie au Character si non déjà appliqué :
     * augmente maxHealth puis soigne.
     *
     * @param character cible qui reçoit le boost de vie
     */
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

