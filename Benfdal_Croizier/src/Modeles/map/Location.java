package Modeles.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import Modeles.characters.Character;
import Modeles.characters.Enemy;
import Modeles.items.consumables.Consumable;

public class Location {
    private Map<Direction, Location> nextFloors = new HashMap<>();
    protected String name;
    protected String description;
    protected int floorLevel;
    protected Difficulty difficulty;
    protected List<Consumable> loot;
    protected List<Character> enemies;
    protected List<Direction> exits;

    public Location(String name, String description, int floorLevel, List<Direction> exits) {
        this.name = name;
        this.description = description;
        this.floorLevel = floorLevel;
        this.difficulty = difficultyFromFloorLevel(floorLevel);

        this.loot = Consumable.getRandomConsumable(1);
        this.enemies = Location.generateEnemies(difficulty);
        this.exits = exits;
    }

    public String getName() {
        return this.name;
    }

    public String getDescription() {
        return this.description;
    }

    public int getFloorLevel() {
        return this.floorLevel;
    }

    public List<Character> getEnemies() {
        return this.enemies;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public List<Consumable> getLoot() {
        return this.loot;
    }

    public List<Direction> getExitDirections() {
        return new ArrayList<Direction>(this.exits);
    }

    private static List<Character> generateEnemies(Difficulty difficulty) {
        List<Character> enemies = new ArrayList<>();
        int enemyCount;

        if (difficulty == Difficulty.EASY) {
            enemyCount = 1;
        } else if (difficulty == Difficulty.NORMAL) {
            enemyCount = 2;
        } else {
            enemyCount = 3;
        }

        for (int i = 0; i < enemyCount; i++) {
            int hp = new Random().nextInt(50) + 50;
            int attack = new Random().nextInt(10) + 5;
            int speed;

            if (difficulty == Difficulty.HARD) {
                speed = 3;
            } else {
                speed = 2;
            }

            enemies.add(new Enemy("Enemy_" + (i + 1), hp, attack, speed));
        }

        return enemies;
    }

    public static Location generateNextFloor(Direction from, int floorLevel) {
        int exitCount = new Random().nextInt(1,4);
        List<Direction> exits = new ArrayList<Direction>();
        List<Direction> directions = new ArrayList<Direction>(Arrays.asList(Direction.values()));
        while(exitCount > 0) {
            int ind = new Random().nextInt(directions.size());
            exits.add(directions.get(ind));
            directions.remove(ind);
            exitCount--;
        }
        if(floorLevel == 9 || floorLevel == 19 || floorLevel == 29) {
            return new BossLocation("Boss floor", "A boss floor", floorLevel+1, exits);
        }
        boolean isLootRoom = new Random().nextDouble() < LootRoom.LOOT_ROOM_SPAWN_CHANCE;
        if(isLootRoom) {
            return new LootRoom("Loot room", "A loot room", floorLevel+1, exits);
        }
        return new Location("Location", "A floor", floorLevel+1, exits);
    }

    private static Difficulty difficultyFromFloorLevel(int floorLevel) {
        if(floorLevel <= 10) return Difficulty.EASY;
        if(floorLevel <= 20) return Difficulty.NORMAL;
        if(floorLevel <= 29) return Difficulty.HARD;
        return Difficulty.IMPOSSIBLE;
    }

    public void displayOnEnter() {
        System.out.println(String.format("You are on floor %d, on %s difficulty !", this.floorLevel, this.difficulty));
    }
    public void generateNextFloors(int currentFloorLevel) {
        for (Direction dir : this.getExitDirections()) {
            Location next = Location.generateNextFloor(Direction.opposite(dir), currentFloorLevel);
            nextFloors.put(dir, next);
        }
    }

    public Location getNextLocation(Direction dir) {
        return nextFloors.get(dir);
    }

    public Map<Direction, Location> getAllNextFloors() {
        return nextFloors;
    }
}
