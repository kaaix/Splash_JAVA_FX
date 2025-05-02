/**
 * Consumable qui augmente la vitesse de déplacement d’un Character.
 * Le boost appliqué est compris entre MIN_VALUE (3) et MAX_VALUE (10).
 */
package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class SpeedBoost extends Consumable {
    
    private static final int MIN_VALUE = 3;
    private static final int MAX_VALUE = 10;
    
    private int speedBoostValue;

    /**
     * Construit un SpeedBoost avec une valeur précise.
     *
     * @param speedBoostValue valeur du bonus de vitesse à appliquer
     */
    public SpeedBoost(int speedBoostValue) {
        super(
                I18N.get("bonus.speed.name"),
                I18N.getFormatted("bonus.speed.desc", speedBoostValue)
        );
        this.speedBoostValue = speedBoostValue;
    }


    /**
     * Construit un SpeedBoost avec une valeur aléatoire
     * entre MIN_VALUE et MAX_VALUE.
     */
    public SpeedBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    /**
     * Applique le bonus de vitesse au Character
     * et marque ce consommable comme appliqué.
     *
     * @param character cible qui reçoit le boost de vitesse
     */
    @Override
    public void useOn(Character character) {
        character.addSpeed(this.speedBoostValue);
        this.markAsApplied(); // ← n’oublie pas ça
    }

    /**
     * Chaîne de la forme "SpeedBoost:<valeur>".
     *
     * @return représentation textuelle incluant la valeur du boost
     */
    @Override
    public String toString() {
        return "SpeedBoost:" + speedBoostValue;
    }


}

