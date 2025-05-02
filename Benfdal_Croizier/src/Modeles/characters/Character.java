/**
 * Classe abstraite représentant un personnage (héros ou ennemi).
 * Contient les attributs communs : nom, points de vie, puissance d’attaque,
 * vitesse et chance de coup critique, ainsi que le maximum de PV.
 */
package Modeles.characters;

public abstract class Character {
    
    protected String name;
    protected int health;
    protected int attackPower;
    protected float speed;
    protected float critChance;
    protected int maxHealth = 100;

    /**
     * Construit un Character avec les paramètres de base.
     *
     * @param name        nom du personnage
     * @param health      points de vie initiaux
     * @param attackPower puissance d’attaque de base
     * @param speed       vitesse de déplacement
     */
    public Character(String name, int health, int attackPower, float speed) {
        this.name = name;
        this.health = health;
        this.attackPower = attackPower;
        this.speed = speed;
        this.critChance = 0;
    }

    /**
     * Construit un Character en précisant aussi la chance de coup critique.
     *
     * @param name        nom du personnage
     * @param health      points de vie initiaux
     * @param attackPower puissance d’attaque de base
     * @param speed       vitesse de déplacement
     * @param critChance  probabilité de coup critique (0.0–1.0)
     */
    public Character(String name, int health, int attackPower, float speed, float critChance) {
        this(name, health, attackPower, speed);
        this.critChance = critChance;
    }

    /**
     * @return le nom du personnage
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return les points de vie actuels
     */
    public int getHealth() {
        return this.health;
    }

    /**
     * @return la puissance d’attaque actuelle
     */
    public int getAttackPower() {
        return this.attackPower;
    }
    /**
     * @return la probabilité de coup critique (0.0–1.0)
     */

    public float getCritChance() {
        return critChance;
    }

    /**
     * @return la vitesse de déplacement
     */
    public float getSpeed() {
        return this.speed;
    }

    /**
     * Ajoute de la vie, sans dépasser la valeur maxHealth.
     * @param val quantité de PV à ajouter
     */
    public void addHealth(int val) {
        this.health = Math.min(this.health + val, this.maxHealth); // ✅ ne jamais dépasser le max
    }


    /**
     * Augmente la puissance d’attaque.
     * @param val quantité à ajouter
     */
    public void addAttackPower(int val) {
        this.attackPower += val;
    }

    /**
     * Augmente la vitesse de déplacement.
     * @param val quantité à ajouter
     */
    public void addSpeed(float val) {
        this.speed += val;
    }

    /**
     * Augmente la chance de coup critique.
     * @param val quantité à ajouter
     */
    public void addCriticalChance(float val) {
        this.critChance += val;
    }

    /**
     * Inflige des dégâts à la cible selon attackPower,
     * avec possibilité de coup critique.
     *
     * @param target le Character à attaquer
     */
    public void attack(Character target) {
        boolean isCrit = Math.random() > critChance;
        int damage = attackPower * 2;
        if (isCrit) {
            damage *= 2;
        }
        target.health -= damage;
        System.out.println(String.format("%s deals %d damage to %s !", this.name, this.attackPower, target.name));
    }

    /**
     * Réinitialise les statistiques au mode par défaut :
     * health = maxHealth, attackPower = 0, speed = 100, critChance = 0.
     */
    public void resetStats() {
        this.health = maxHealth;
        this.attackPower = 0;
        this.speed = 100;
        this.critChance = 0;
    }

    /**
     * @return le nombre maximal de points de vie
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Augmente le maximum de points de vie.
     * @param val quantité à ajouter au maxHealth
     */
    public void addMaxHealth(int val) {
        this.maxHealth += val;
    }

    /**
     * Réduit les points de vie de la valeur donnée (>= 0).
     * @param amount dégâts à appliquer
     */
    public void takeDamage(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    /**
     * Définit les points de vie, bornés entre 0 et maxHealth.
     * @param health nouvelle valeur de PV
     */
    public void setHealth(int health) {
        this.health = Math.max(0, Math.min(health, this.maxHealth));
    }

    /**
     * Définit le maximum de points de vie.
     * @param val nouvelle valeur de maxHealth
     */
    public void setMaxHealth(int val) {
        this.maxHealth = val;
    }

}