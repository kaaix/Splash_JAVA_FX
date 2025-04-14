package Modeles.characters;

import java.util.List;

import Modeles.items.Bag;
import Modeles.items.Item;
import Modeles.items.consumables.Consumable;
import Modeles.items.weapons.Weapon;
import Modeles.map.Location;

public class Hero extends Character {

    private static final int DEFAULT_HEALTH = 100;
    private static final int DEFAULT_ATTACK_POWER = 100;
    private static final int DEFAULT_SPEED = 100;

    private Weapon weapon;
    private Bag bag;

    public Hero(String name, int health, Weapon weapon) {
        super(name, DEFAULT_HEALTH, DEFAULT_ATTACK_POWER, DEFAULT_SPEED);
        equipWeapon(weapon);
        this.bag = new Bag();
    }
    public Hero(String name, Weapon weapon) {
        this(name, DEFAULT_HEALTH, weapon);
    }

    public Weapon getWeaponName() {
        return this.weapon;
    }

    public void equipWeapon(Weapon weapon) {
        this.weapon = weapon;
        this.attackPower = weapon.getFirePower();
        this.speed = weapon.getFireRate();
    }

    public boolean use(Consumable item) {
        if(this.bag.contains(item)) {
            item.useOn(this);
            this.bag.remove(item);
            return true;
        }
        return false;
    }

    public void storeItem(Item item) {
        this.bag.store(item);
    }

    public void removeItem(Item item) {
        this.bag.remove(item);
    }

    public void displayInventory() {
        List<Item> content = this.bag.getContent();
        System.out.println("Your inventory: ");
        System.out.print(content.getFirst().toString());
        for (int i = 1; i < content.size(); i++) {
            System.out.print(" | " + content.get(i).toString());
        }
        System.out.println();
    }

    public void playFloor(Location floor) {
        floor.displayOnEnter();

        List<Character> enemies = floor.getEnemies();
        for(Character enemy : enemies) {
            this.fight(enemy);
        }

        List<Consumable> loot = floor.getLoot();
        for(Consumable item : loot) {
            if(this.bag.store(item)) {
                System.out.println(String.format("You picked up a %s !", item.toString()));
            } else {
                System.out.println(String.format("Your bag is full. You leave behind a %s.", item.toString()));   
            }
        }
    }

   private boolean fight(Character enemy) {
    int round = 1;
    while(enemy.getHealth() > 0 && this.health > 0) {
      
        System.out.println("-------------------------");
        System.out.println("Round " + round + ":");
        this.attack(enemy);
        if(enemy.health > 0) {
            enemy.attack(this);
        }
        round++;
        System.out.println("-------------------------");
        
    }
    System.out.println("");
    return this.health > 0;
}


}
