package Modeles.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Modeles.characters.Character;
import Modeles.characters.boss.Boss1;
import Modeles.characters.boss.Boss2;
import Modeles.characters.boss.Boss3;
import Modeles.characters.boss.FinalBoss;

public class BossLocation extends Location {

    public Map<Difficulty, Character> DifficultyBossMap = Map.of(
        Difficulty.EASY, new Boss1(),
        Difficulty.NORMAL, new Boss2(),
        Difficulty.HARD, new Boss3(),
        Difficulty.IMPOSSIBLE, new FinalBoss()
    );

    public BossLocation(String name, String description, int floorLevel, List<Direction> exits) {
        super(name, description, floorLevel, exits);
        this.enemies = new ArrayList<Character>();
        Character boss = DifficultyBossMap.get(difficulty);
        this.enemies.add(boss);
    }

    
    @Override
    public void displayOnEnter() {
        System.out.println(String.format("You are on a boss floor !"));
    }
    

    
}
