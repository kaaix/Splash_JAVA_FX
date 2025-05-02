/**
 * Consumable qui augmente la puissance d’attaque d’un Character.
 * Le boost appliqué est compris entre MIN_VALUE (5) et MAX_VALUE (15).
 */
package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class AttackPowerBoost extends Consumable {

    private static final int MIN_VALUE = 5;
    private static final int MAX_VALUE = 15;

    /**
     * Construit un AttackPowerBoost avec une valeur précise.
     *
     * @param attackPowerBoostValue valeur du bonus d’attaque à appliquer
     */
    private int attackPowerBoostValue;

    public AttackPowerBoost(int attackPowerBoostValue) {
        super(
                I18N.get("bonus.attack.name"),
                I18N.getFormatted("bonus.attack.desc", attackPowerBoostValue)
        );
        this.attackPowerBoostValue = attackPowerBoostValue;
    }

    /**
     * Construit un AttackPowerBoost avec une valeur aléatoire
     * entre MIN_VALUE et MAX_VALUE.
     */
    public AttackPowerBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    /**
     * Applique le bonus d’attaque au Character spécifié
     * et marque ce consommable comme appliqué.
     *
     * @param character cible qui reçoit le boost d’attaque
     */
    @Override
    public void useOn(Character character) {
        character.addAttackPower(this.attackPowerBoostValue);
        this.markAsApplied();
    }

    /**
     * Chaîne de la forme "AttackPowerBoost:<valeur>".
     *
     * @return représentation textuelle incluant la valeur du boost
     */
    @Override
    public String toString() {
        return "AttackPowerBoost:" + attackPowerBoostValue;
    }

}
