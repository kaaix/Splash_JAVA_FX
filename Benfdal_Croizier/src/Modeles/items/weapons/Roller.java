/**
 * Arme de type Roller : une arme de mêlée à cadence moyenne.
 * Puissance de tir : 70, cadence : 1.2 tirs/sec, portée : 50.
 */
package Modeles.items.weapons;

public class Roller extends Weapon {

    /**
     * Construit une arme Roller avec sa configuration par défaut :
     * nom "Roller", description, firePower 70, fireRate 1.2, portee 50.
     */
    public Roller() {
        super("Roller", "A melee weapon", 70,1.2,50 );
    }

}
