package Modeles.map;

import java.util.*;
import Modeles.characters.Character;
import Modeles.characters.Enemy;
import Modeles.characters.boss.*;
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

    private static final List<Class<? extends Enemy>> AVAILABLE_BOSSES = new ArrayList<>(List.of(
            Boss1.class, Boss2.class, Boss3.class
    ));
    private static final List<Class<? extends Enemy>> PICKED_BOSSES = new ArrayList<>();


    public Location(String name, String description, int floorLevel, List<Direction> exits) {
        this.name = name;
        this.description = description;
        this.floorLevel = floorLevel;
        this.difficulty = difficultyFromFloorLevel(floorLevel);
        this.exits = exits;

        this.loot = Consumable.getRandomConsumable(1);
        this.enemies = generateEnemies(difficulty, floorLevel);
    }

    public Location(int floorLevel, String description) {
        this(floorLevel == 10 || floorLevel == 20 || floorLevel == 30 ? "Boss Floor" : "Floor " + floorLevel,
                description,
                floorLevel,
                Arrays.asList(Direction.values()));
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
        return new ArrayList<>(this.exits);
    }

    private static List<Character> generateEnemies(Difficulty difficulty, int floorLevel) {
        List<Character> enemies = new ArrayList<>();
        Random random = new Random();

        if (floorLevel == 10 || floorLevel == 20) {
            List<Class<? extends Enemy>> candidates = new ArrayList<>(AVAILABLE_BOSSES);
            candidates.removeAll(PICKED_BOSSES);

            if (!candidates.isEmpty()) {
                Class<? extends Enemy> bossClass = candidates.get(random.nextInt(candidates.size()));
                PICKED_BOSSES.add(bossClass);

                try {
                    enemies.add(bossClass.getDeclaredConstructor().newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                System.err.println("❌ Tous les boss ont déjà été utilisés !");
            }

        } else if (floorLevel == 30) {
            enemies.add(new FinalBoss());

        } else {
            int enemyCount = switch (difficulty) {
                case EASY -> 1;
                case NORMAL -> 2;
                default -> 3;
            };

            Enemy base = switch (difficulty) {
                case EASY -> Enemy.enemy1;
                case NORMAL -> Enemy.enemy2;
                default -> Enemy.enemy3; // HARD ou IMPOSSIBLE
            };

            for (int i = 0; i < enemyCount; i++) {
                enemies.add(new Enemy("Enemy_" + (i + 1), base.getHealth(), base.getAttackPower(), base.getSpeed()));
            }
        }

        return enemies;
    }
    public static Location generateNextFloor(Direction from, int floorLevel) {
        int exitCount = new Random().nextInt(1, 4);
        List<Direction> exits = new ArrayList<>();
        List<Direction> directions = new ArrayList<>(Arrays.asList(Direction.values()));

        while (exitCount > 0) {
            int ind = new Random().nextInt(directions.size());
            exits.add(directions.get(ind));
            directions.remove(ind);
            exitCount--;
        }

        return new Location("Generated Floor", "Auto-generated", floorLevel + 1, exits);
    }

    private static Difficulty difficultyFromFloorLevel(int floorLevel) {
        if (floorLevel <= 10) return Difficulty.EASY;
        if (floorLevel <= 20) return Difficulty.NORMAL;
        if (floorLevel <= 29) return Difficulty.HARD;
        return Difficulty.IMPOSSIBLE;
    }

    public void displayOnEnter() {
        if (floorLevel == 10 || floorLevel == 20 || floorLevel == 30) {
            System.out.println("⚔️ You are entering a BOSS floor!");
        } else {
            System.out.printf("You are on floor %d, difficulty: %s%n", floorLevel, difficulty);
        }
    }

    public Location getNextLocation(Direction dir) {
        return nextFloors.get(dir);
    }
}
