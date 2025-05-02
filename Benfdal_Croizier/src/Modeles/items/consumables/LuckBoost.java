package Modeles.items.consumables;

import java.util.Random;

import Modeles.characters.Character;
import utils.I18N;

public class LuckBoost extends Consumable {
    
    private static final int MIN_VALUE = 10;
    private static final int MAX_VALUE = 20;
    
    private int luckBoostValue;

    public LuckBoost(int luckBoostValue) {
        super(
                I18N.get("bonus.luck.name"),
                I18N.getFormatted("bonus.luck.desc", luckBoostValue)
        );
        this.luckBoostValue = luckBoostValue;
    }


    public LuckBoost() {
        this(new Random().nextInt(MIN_VALUE, MAX_VALUE));
    }

    @Override
    public void useOn(Character character) {
        System.out.println("🎯 LuckBoost appliqué (+" + luckBoostValue + "%)");
        character.addCriticalChance(luckBoostValue);
    }

    @Override
    public String toString() {
        return "LuckBoost:" + luckBoostValue;
    }
    
}