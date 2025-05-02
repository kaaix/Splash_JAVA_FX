package Modeles.characters;

public abstract class Character {
    
    protected String name;
    protected int health;
    protected int attackPower;
    protected float speed;
    protected float critChance;
    protected int maxHealth = 100;

    public Character(String name, int health, int attackPower, float speed) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.speed = speed;
        this.critChance = 0;
    }

    public Character(String name, int health, int attackPower, float speed, float critChance) {
        this(name, health, attackPower, speed);
        this.critChance = critChance;
    }

    public String getName() {
        return this.name;
    }

    public int getHealth() {
        return this.health;
    }

    public int getAttackPower() {
        return this.attackPower;
    }

    public float getCritChance() {
        return critChance;
    }


    public float getSpeed() {
        return this.speed;
    }

    public void addHealth(int val) {
        this.health = Math.min(this.health + val, this.maxHealth); // ✅ ne jamais dépasser le max
    }


    public void addAttackPower(int val) {
        this.attackPower += val;
    }

    public void addSpeed(float val) {
        this.speed += val;
    }

    public void addCriticalChance(float val) {
        this.critChance += val;
    }


    public void attack(Character target) {
        boolean isCrit = Math.random() > critChance;
        int damage = attackPower * 2;
        if (isCrit) {
            damage *= 2;
        }
        target.health -= damage;
        System.out.println(String.format("%s deals %d damage to %s !", this.name, this.attackPower, target.name));
    }

    public void resetStats() {
        this.health = maxHealth;
        this.attackPower = 0;
        this.speed = 100;
        this.critChance = 0;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void addMaxHealth(int val) {
        this.maxHealth += val;
    }


    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
    }

    public void setMaxHealth(int val) {
        this.maxHealth = val;
    }

}