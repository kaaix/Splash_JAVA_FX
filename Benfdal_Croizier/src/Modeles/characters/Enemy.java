/**
 * Représente un type d’ennemi dans le jeu, avec des instances préconfigurées.
 * Hérite de {@link Character} pour gérer PV, attaque, vitesse et critique.
 */
package Modeles.characters;

public class Enemy extends Character {

    /** Prototype d’ennemi faible (200 PV, 20 ATK, vitesse 50). */
    public static Enemy enemy1 = new Enemy(200, 20,  50);
    /** Prototype d’ennemi moyen (300 PV, 40 ATK, vitesse 70). */
    public static Enemy enemy2 = new Enemy(300, 40, 70);
    /** Prototype d’ennemi fort (500 PV, 60 ATK, vitesse 90). */
    public static Enemy enemy3 = new Enemy(500, 60, 90);

    /**
     * Crée un ennemi par défaut nommé "Enemy" avec des caractéristiques données.
     * Définit aussi {@code maxHealth} pour assurer la bonne valeur maximale de PV.
     *
     * @param health     points de vie initiaux et maximum
     * @param attackPower puissance d’attaque
     * @param speed       vitesse de déplacement (float)
     */
    public Enemy(int health, int attackPower, float speed) {
        this("Enemy", health, attackPower, speed);
        this.maxHealth = health; // ✅ corrige le bug ici
    }

    /**
     * Crée un ennemi avec un nom personnalisé et des caractéristiques données.
     * Définit aussi {@code maxHealth} pour assurer la bonne valeur maximale de PV.
     *
     * @param name        nom de l’ennemi
     * @param health      points de vie initiaux et maximum
     * @param attackPower puissance d’attaque
     * @param speed       vitesse de déplacement (float)
     */
    public Enemy(String name, int health, int attackPower, float speed) {
        super(name, health, attackPower, speed);
        this.maxHealth = health; // ✅ aussi ici au cas où
    }

}
