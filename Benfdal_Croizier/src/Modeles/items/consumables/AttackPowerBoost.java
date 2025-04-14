package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;

public class AttackPowerBoost extends Consumable {

    private static final int MIN_VALUE = 5;
    private static final int MAX_VALUE = 15;

    private int attackPowerBoostValue;

    public AttackPowerBoost(int attackPowerBoostValue) {
        super("Attack power boost", String.format("Boosts user's attack power by %d points.", attackPowerBoostValue));
        this.attackPowerBoostValue = attackPowerBoostValue;
    }

    public AttackPowerBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }    
    
        @Override
    public void useOn(Character character) {
        character.addAttackPower(this.attackPowerBoostValue);
    }
    
}
