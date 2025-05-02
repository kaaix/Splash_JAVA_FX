/**
 * Boss final ultime.
 * HP = 3000, ATK = 200, vitesse = 70.
 */
package Modeles.characters.boss;

import Modeles.characters.Enemy;

public class FinalBoss extends Enemy {

    /**
     * Construit FinalBoss avec nom "Final boss", 3000 PV, 200 ATK et vitesse 70.
     */
    public FinalBoss() {
        super("Final boss", 3000, 200, 70);
    }

}
