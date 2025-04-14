package Modeles.map;

import java.util.ArrayList;
import java.util.List;

import Modeles.characters.Character;
import Modeles.items.consumables.Consumable;

public class LootRoom extends Location {

    public final static double LOOT_ROOM_SPAWN_CHANCE =  0.10 ;

    public LootRoom(String name, String description, int floorLevel, List<Direction> exits) {
        super(name, description, floorLevel, exits);
        this.loot = Consumable.getRandomConsumable(2);
        this.enemies = new ArrayList<Character>();
        this.difficulty = Difficulty.EASY;
    }

    @Override
    public List<Character> getEnemies() {
        return new ArrayList<Character>();
    }


    @Override
    public void displayOnEnter() {
        System.out.println(String.format("You entered a loot room !"));
    }



}
