package Modeles.characters;

public class Enemy extends Character {
    
    public static Enemy enemy1 = new Enemy(200, 20,  50);
    public static Enemy enemy2 = new Enemy(300, 40, 70);
    public static Enemy enemy3 = new Enemy(500, 60, 90);

    public Enemy(int health, int attackPower, float speed) {
        this("Enemy", health, attackPower, speed);
        this.maxHealth = health; // ✅ corrige le bug ici
    }

    public Enemy(String name, int health, int attackPower, float speed) {
        super(name, health, attackPower, speed);
        this.maxHealth = health; // ✅ aussi ici au cas où
    }

}
