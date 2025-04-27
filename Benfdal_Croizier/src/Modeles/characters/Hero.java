package Modeles.characters;

import Modeles.game.GameModel;
import Modeles.items.Bag;
import Modeles.items.weapons.Weapon;

public class Hero extends Character {

    private static final int DEFAULT_HEALTH = 100;
    private static final int DEFAULT_ATTACK_POWER = 100;
    private static final int DEFAULT_SPEED = 100;

    private Weapon weapon;
    private Bag bag;
    private GameModel gameModel; // Référence à GameModel

    public Hero(String name, int health, Weapon weapon, GameModel gameModel) {
        super(name, DEFAULT_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_SPEED);
        this.bag = new Bag();
        this.gameModel = gameModel; // Initialiser GameModel
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
}
