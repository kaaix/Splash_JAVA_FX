/**
 * Arme de type Charger : une arme chargée à courte portée avec dégâts élevés.
 * Puissance de tir : 90, cadence : 0.6 tirs/sec, portée : 200.
 */
package Modeles.items.weapons;

public class Charger extends Weapon {

    /**
     * Construit une arme Charger avec sa configuration par défaut :
     * nom "Charger", description, firePower 90, fireRate 0.6, portee 200.
     */
    public Charger() {
        super("Charger", "A close range charged weapon.", 90,0.6,200 );
    }

}
