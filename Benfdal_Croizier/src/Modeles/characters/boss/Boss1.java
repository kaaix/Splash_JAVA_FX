/**
 * Premier boss du donjon.
 * HP = 300, ATK = 100, vitesse = 80.
 */
package Modeles.characters.boss;

import Modeles.characters.Enemy;

public class Boss1 extends Enemy {
    /**
     * Construit Boss1 avec nom "Boss 1", 300 PV, 100 ATK et vitesse 80.
     */
    public Boss1() {
        super("Boss 1", 300, 100, 80); // HP, atk, speed
    }
}


