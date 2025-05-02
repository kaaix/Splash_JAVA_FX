/**
 * Représente le héros du joueur dans le jeu.
 * Hérite de Character et gère l’inventaire, l’arme équipée
 * et la liaison avec le modèle de jeu (GameModel).
 */
package Modeles.characters;

import Modeles.game.GameModel;
import Modeles.items.Bag;
import Modeles.items.IContainer;
import Modeles.items.Item;
import Modeles.items.consumables.Consumable;
import Modeles.items.weapons.Weapon;

public class Hero extends Character {

    private static final int DEFAULT_HEALTH = 100;
    private static final int DEFAULT_SPEED = 100;

    private Weapon weapon;
    private Bag bag;
    private GameModel gameModel; // Référence à GameModel

    /**
     * Construit un héros nommé avec une arme donnée et lie
     * le modèle de jeu pour la position et la taille de case.
     *
     * @param name      nom du héros
     * @param health    points de vie initiaux (ignored – DEFAULT_HEALTH utilisé)
     * @param weapon    arme équipée du héros
     * @param gameModel référence au GameModel pour la position et le déplacement
     */
    public Hero(String name, int health, Weapon weapon, GameModel gameModel) {
        super(name, DEFAULT_HEALTH, 0, DEFAULT_SPEED);
        this.bag = new Bag();
        this.gameModel = gameModel; // Initialiser GameModel
        this.weapon = weapon;
    }

    /**
     * Construit un héros avec points de vie par défaut et une arme,
     * lie également le modèle de jeu pour la position.
     *
     * @param name      nom du héros
     * @param weapon    arme équipée
     * @param gameModel modèle de jeu pour la position
     */
    public Hero(String name, Weapon weapon, GameModel gameModel) {
        this(name, DEFAULT_HEALTH, weapon, gameModel);
    }

    /**
     * @return la coordonnée X courante du héros (pixels)
     */
    public double getX() {
        return this.gameModel.getPlayerX(); // Utiliser GameModel pour obtenir la position X
    }

    /**
     * @return la coordonnée Y courante du héros (pixels)
     */
    public double getY() {
        return this.gameModel.getPlayerY(); // Utiliser GameModel pour obtenir la position Y
    }

    /**
     * Déplace le héros d’une case vers la droite.
     */
    public void moveRight() {
        double newX = this.getX() + gameModel.getTailleCase();
        gameModel.movePlayer(newX, this.getY());
    }

    /**
     * Déplace le héros d’une case vers le haut.
     */
    public void moveUp() {
        double newY = this.getY() - gameModel.getTailleCase();
        gameModel.movePlayer(this.getX(), newY);
    }

    /**
     * Déplace le héros d’une case vers la gauche.
     */
    public void moveLeft() {
        double newX = this.getX() - gameModel.getTailleCase();
        gameModel.movePlayer(newX, this.getY());
    }

    /**
     * Déplace le héros d’une case vers le bas.
     */
    public void moveDown() {
        double newY = this.getY() + gameModel.getTailleCase();
        gameModel.movePlayer(this.getX(), newY);
    }

    /**
     * Ajoute un consommable à l’inventaire du héros.
     *
     * @param c le consommable à stocker
     */
    public void addConsumable(Consumable c) {
        bag.store(c);          // Et l'ajoute au sac si tu veux le garder en inventaire
    }

    /**
     * Réinitialise les stats puis réapplique tous les bonus
     * contenus dans le sac (bag) au héros.
     */
    public void reapplyBonuses() {

        resetStats(); // 👈 remet à zéro avant d’appliquer

        for (Item item : bag.getContent()) {
            if (item instanceof Consumable c) {
                System.out.println("↪ Reapplying: " + c.getName());
                c.useOn(this); // applique
                c.markAsApplied(); // définit comme déjà appliqué
            }
        }


        System.out.println("📊 Stats après bonus :");
        System.out.println("  ➤ Attack: " + this.attackPower);
        System.out.println("  ➤ Speed: " + this.speed);
        System.out.println("  ➤ Crit: " + this.critChance);
    }

    /**
     * Réinitialise les statistiques de base et réapplique les bonus.
     */
    public void refreshStats() {
        this.resetStats();
        this.reapplyBonuses();
    }

    /**
     * Réduit les points de vie du héros en évitant de descendre en dessous de zéro.
     *
     * @param amount dégâts à appliquer
     */
    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }

    /**
     * @return l’arme actuellement équipée par le héros
     */
    public Weapon getWeapon() {
        return weapon;
    }

    /**
     * @return l’inventaire (bag) du héros
     */
    public IContainer getBag() {
        return bag;
    }

    /**
     * Modifie la puissance d’attaque du héros.
     *
     * @param attackPower nouvelle valeur d’attaque
     */
    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    /**
     * Modifie le nom du héros.
     *
     * @param nomHero nouveau nom
     */
    public void setName(String nomHero) {
        this.name = nomHero;
    }

    /**
     * Change l’arme équipée du héros.
     *
     * @param weapon nouvelle arme
     */
    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

    /**
     * Attaque une cible en appliquant les dégâts et
     * gère la probabilité de coup critique.
     *
     * @param target Character à attaquer
     */
    @Override
    public void attack(Character target) {
        int totalDamage = this.attackPower;

        boolean isCrit = Math.random() < (this.critChance / 100.0); // 👈 conversion %
        int damage = isCrit ? totalDamage * 2 : totalDamage;

        target.addHealth(-damage);
        System.out.printf(
                "%s attaque %s ➤ %s %d dégâts (critChance = %.2f%%)%n",
                this.name,
                target.getName(),
                (isCrit ? "⚡ CRIT" : "☄️ Hit"),
                damage,
                this.critChance
        );
    }

    /**
     * Réinitialise les stats de base et ajoute l’effet de l’arme équipée.
     */
    @Override
    public void resetStats() {
        super.resetStats(); // reset les stats de base

        // ✅ réapplique l’effet de l’arme (attaque, vitesse, etc.)
        if (weapon != null) {
            this.addAttackPower(weapon.getFirePower());
        }
    }


}
