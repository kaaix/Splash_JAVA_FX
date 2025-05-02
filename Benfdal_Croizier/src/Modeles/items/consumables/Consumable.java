/**
 * Classe abstraite représentant un consommable pouvant être appliqué
 * à un personnage (Character).
 * Les consommables étendent Item et définissent une méthode useOn()
 * pour appliquer leur effet. Cette classe fournit également
 * des utilitaires pour obtenir des consommables aléatoires
 * et pour parser un consommable depuis son nom.
 */
package Modeles.items.consumables;

import Modeles.items.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import Modeles.characters.Character;

public abstract class Consumable extends Item {

    /**
     * Construit un consommable avec le nom et la description donnés.
     *
     * @param displayName le nom à afficher du consommable
     * @param description la description textuelle de son effet
     */
    public Consumable(String displayName, String description) {
        super(displayName, description);
    }

    /**
     * Applique l’effet de ce consommable sur le personnage spécifié.
     * Doit être implémenté par chaque sous-classe pour définir
     * son comportement propre.
     *
     * @param character le personnage qui reçoit l’effet
     */
    public abstract void useOn(Character character);

    protected boolean alreadyApplied = false;

    /**
     * Renvoie un consommable choisi aléatoirement parmi les types disponibles.
     *
     * @return une instance nouvellement créée d’un sous-type de Consumable
     */
    public static Consumable getRandomConsumable() {
        List<Consumable> possibility = new ArrayList<Consumable>();
        Collections.addAll(possibility, 
            new AttackPowerBoost(), 
            new HealthBoost(),
            new LuckBoost(),
            new SpeedBoost());
        return possibility.get(new Random().nextInt(possibility.size()));
    }

    /**
     * Renvoie une liste de n consommables aléatoires.
     *
     * @param n le nombre de consommables à générer
     * @return liste de n instances de sous-classes de Consumable
     */
    public static List<Consumable> getRandomConsumable(int n) {
        List<Consumable> res = new ArrayList<Consumable>();
        for(int i = 0 ; i < n ; i++) {
            res.add(getRandomConsumable());
        }
        return res;
    }

    /**
     * Indique si ce consommable a déjà été appliqué
     * (pour éviter de le réutiliser plusieurs fois).
     *
     * @return true si useOn() a déjà été appelé, false sinon
     */
    public boolean isAlreadyApplied() {
        return alreadyApplied;
    }

    /**
     * Marque ce consommable comme appliqué,
     * afin que isAlreadyApplied() renvoie true par la suite.
     */
    public void markAsApplied() {
        this.alreadyApplied = true;
    }

    /**
     * Construit un consommable à partir de son nom encodé
     * (par exemple "AttackPowerBoost:5").
     *
     * @param name la chaîne identifiant le type de boost et sa valeur
     * @return le Consumable correspondant, ou null si la chaîne n’est pas reconnue
     */
    public static Consumable parseFromName(String name) {
        if (name.startsWith("LuckBoost:")) {
            int value = Integer.parseInt(name.split(":")[1]);
            return new LuckBoost(value);
        }

        if (name.startsWith("AttackPowerBoost:")) {
            int value = Integer.parseInt(name.split(":")[1]);
            return new AttackPowerBoost(value);
        }

        if (name.startsWith("SpeedBoost:")) {
            int value = Integer.parseInt(name.split(":")[1]);
            return new SpeedBoost(value);
        }

        if (name.startsWith("HealthBoost:")) {
            int value = Integer.parseInt(name.split(":")[1]);
            return new HealthBoost(value);
        }

        return null;
    }

    /**
     * Définit manuellement l’état appliqué de ce consommable.
     *
     * @param b true pour marquer comme déjà appliqué, false pour réinitialiser
     */
    public void setAlreadyApplied(boolean b) {
        this.alreadyApplied = b;
    }
}
