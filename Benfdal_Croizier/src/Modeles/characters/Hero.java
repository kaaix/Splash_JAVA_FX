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

    public Hero(String name, int health, Weapon weapon, GameModel gameModel) {
        super(name, DEFAULT_HEALTH, 0, DEFAULT_SPEED);
        this.bag = new Bag();
        this.gameModel = gameModel; // Initialiser GameModel
        this.weapon = weapon;
    }

    public Hero(String name, Weapon weapon, GameModel gameModel) {
        this(name, DEFAULT_HEALTH, weapon, gameModel);
    }

    public double getX() {
        return this.gameModel.getPlayerX(); // Utiliser GameModel pour obtenir la position X
    }

    public double getY() {
        return this.gameModel.getPlayerY(); // Utiliser GameModel pour obtenir la position Y
    }

    // Déplacer le héros à droite
    public void moveRight() {
        double newX = this.getX() + gameModel.getTailleCase();
        gameModel.movePlayer(newX, this.getY());
    }

    // Déplacer le héros vers le haut
    public void moveUp() {
        double newY = this.getY() - gameModel.getTailleCase();
        gameModel.movePlayer(this.getX(), newY);
    }

    // Déplacer le héros vers la gauche
    public void moveLeft() {
        double newX = this.getX() - gameModel.getTailleCase();
        gameModel.movePlayer(newX, this.getY());
    }

    // Déplacer le héros vers le bas
    public void moveDown() {
        double newY = this.getY() + gameModel.getTailleCase();
        gameModel.movePlayer(this.getX(), newY);
    }

    public void addConsumable(Consumable c) {
        bag.store(c);          // Et l'ajoute au sac si tu veux le garder en inventaire
    }

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



    public void refreshStats() {
        this.resetStats();
        this.reapplyBonuses();
    }




    public void takeDamage(int amount) {
        health = Math.max(0, health - amount);
    }


    public Weapon getWeapon() {
        return weapon;
    }

    public IContainer getBag() {
        return bag;
    }

    public void setAttackPower(int attackPower) {
        this.attackPower = attackPower;
    }

    public void setName(String nomHero) {
        this.name = nomHero;
    }

    public void setWeapon(Weapon weapon) {
        this.weapon = weapon;
    }

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

    @Override
    public void resetStats() {
        super.resetStats(); // reset les stats de base

        // ✅ réapplique l’effet de l’arme (attaque, vitesse, etc.)
        if (weapon != null) {
            this.addAttackPower(weapon.getFirePower());
        }
    }


}
