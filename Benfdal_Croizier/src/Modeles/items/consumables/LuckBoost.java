package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;

public class LuckBoost extends Consumable {
    
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 20;
    
    private int luckBoostValue;

    public LuckBoost(int luckBoostValue) {
        super("Luck boost", String.format("Boosts user's critical chance by %d points.", luckBoostValue));
        this.luckBoostValue = luckBoostValue;
    }

    public LuckBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }
    
        @Override
    public void useOn(Character character) {
        character.addCriticalChance(this.luckBoostValue);
    }
    
}