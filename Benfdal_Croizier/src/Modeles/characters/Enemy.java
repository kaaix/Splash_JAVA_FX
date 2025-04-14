package Modeles.characters;

public class Enemy extends Character {
    
    public static Enemy enemy1 = new Enemy(5, 5, 3.0f);
    public static Enemy enemy2 = new Enemy(10, 3, 4.0f);
    public static Enemy enemy3 = new Enemy(6, 4, 5.0f);

    public Enemy(String name, int health, int attackPower, float speed) {
        super(name, health, attackPower, speed);
    }

    public Enemy(int health, int attackPower, float speed) {
        this("Enemy", health, attackPower, speed);
    }

}
