/**
 * Consumable qui augmente la chance de coup critique d’un Character.
 * Le boost appliqué est compris entre MIN_VALUE (10) et MAX_VALUE (20).
 */
package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class LuckBoost extends Consumable {
    
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 20;
    
    private int luckBoostValue;

    /**
     * Construit un LuckBoost avec une valeur précise.
     *
     * @param luckBoostValue pourcentage de bonus de coup critique
     */
    public LuckBoost(int luckBoostValue) {
        super(
                I18N.get("bonus.luck.name"),
                I18N.getFormatted("bonus.luck.desc", luckBoostValue)
        );
        this.luckBoostValue = luckBoostValue;
    }


    /**
     * Construit un LuckBoost avec une valeur aléatoire
     * entre MIN_VALUE et MAX_VALUE.
     */
    public LuckBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    /**
     * Applique le bonus de chance de critique au Character.
     *
     * @param character cible qui reçoit le boost de critique
     */
    @Override
    public void useOn(Character character) {
        System.out.println("🎯 LuckBoost appliqué (+" + luckBoostValue + "%)");
        character.addCriticalChance(luckBoostValue);
    }

    /**
     * Chaîne de la forme "LuckBoost:<valeur>".
     *
     * @return représentation textuelle incluant la valeur du boost
     */
    @Override
    public String toString() {
        return "LuckBoost:" + luckBoostValue;
    }
    
}