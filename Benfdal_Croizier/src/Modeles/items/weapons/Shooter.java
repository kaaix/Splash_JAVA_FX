/**
 * Arme de type Shooter : une arme à distance à tir rapide.
 * Puissance de tir : 50, cadence : 2.5 tirs/sec, portée : 100.
 */
package Modeles.items.weapons;

public class Shooter extends Weapon {

    /**
     * Construit une arme Shooter avec sa configuration par défaut :
     * nom "Shooter", description, firePower 50, fireRate 2.5, portee 100.
     */
    public Shooter() {
        super("Shooter", "A rapid-fire ranged weapon", 50,2.5,100 );
    }
}

