/**
 * Classe représentant une arme du jeu.
 * Hérite de Item et ajoute les attributs spécifiques :
 * puissance de tir, cadence de tir et portée.
 */
package Modeles.items.weapons;

import Modeles.items.Item;

public class Weapon extends Item {

    protected int firePower;
    protected double fireRate;
    private int portee;

    /**
     * Construit une arme avec nom, description, puissance, cadence et portée.
     *
     * @param displayName nom affiché de l’arme
     * @param description description textuelle de l’arme
     * @param firePower   puissance des tirs (dégâts)
     * @param fireRate    cadence de tir (tirs par seconde)
     * @param portee      portée des tirs (en cases)
     */
    public Weapon(String displayName, String description, int firePower, double fireRate,int portee) {
        super(displayName, description);
        this.firePower = firePower;
        this.fireRate = fireRate;
        this.portee = portee;
    }

    /**
     * Retourne la puissance de tir de l’arme.
     *
     * @return dégâts infligés par tir
     */
    public int getFirePower() {
        return this.firePower;
    }

    /**
     * Retourne la cadence de tir de l’arme.
     *
     * @return nombre de tirs autorisés par seconde
     */
    public double getFireRate() {
        return this.fireRate;
    }

    /**
     * Crée une instance d’arme à partir de son identifiant.
     *
     * @param weaponStr chaîne ("Shooter", "Roller" ou "Charger")
     * @return nouvelle instance correspondante, ou null si non reconnu
     */
    public static Weapon parseFromString(String weaponStr) {
        switch(weaponStr) {
            case "Shooter": return new Shooter();
            case "Roller": return new Roller();
            case "Charger": return new Charger();
            default: return null;
        }
    }

    /**
     * Retourne la portée de l’arme.
     *
     * @return portée des tirs en nombre de cases
     */
    public int getPortee() {
        return this.portee;
    }

}
