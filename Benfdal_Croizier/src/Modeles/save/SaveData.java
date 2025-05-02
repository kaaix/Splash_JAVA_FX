/**
 * Représente l’état complet d’une partie sauvegardée.
 * Implémente Serializable pour pouvoir être écrit/lu depuis un flux.
 */
package Modeles.save;

import java.io.Serializable;
import java.util.List;

public class SaveData implements Serializable {
    public int floorLevel;
    public double playerX, playerY;
    public int currentHp;
    public String weapon;
    public List<String> bagItems;
    public int timeSeconds;

    public String nomHero;
    public int maxHp;

    /**
     * Construit une instance de SaveData avec l’état de la partie.
     *
     * @param floor        niveau d’étage courant
     * @param x            position X du joueur
     * @param y            position Y du joueur
     * @param hp           points de vie actuels
     * @param weapon       identifiant de l’arme équipée
     * @param bagItems     liste des objets en inventaire
     * @param timeSeconds  temps de jeu écoulé en secondes
     * @param nomHero      nom du héros
     * @param maxHp        points de vie maximum du héros
     */
    public SaveData(int floor, double x, double y, int hp, String weapon, List<String> bagItems, int timeSeconds, String nomHero, int maxHp) {
        this.floorLevel = floor;
        this.playerX = x;
        this.playerY = y;
        this.currentHp = hp;
        this.weapon = weapon;
        this.bagItems = bagItems;
        this.timeSeconds = timeSeconds;

        this.nomHero = nomHero;
        this.maxHp = maxHp; // ✅ nouvelle ligne
    }

}
