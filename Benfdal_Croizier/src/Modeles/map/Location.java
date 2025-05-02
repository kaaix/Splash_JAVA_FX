/**
 * Représente une case (étage) du donjon, avec son nom, sa description,
 * ses ennemis, son butin, sa difficulté et les directions possibles
 * pour passer à d’autres étages.
 */
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


    /**
     * Construit une Location avec propriétés entièrement personnalisées.
     *
     * @param name        nom de la location (ex. "Floor 1" ou "Boss Floor")
     * @param description description textuelle de l’emplacement
     * @param floorLevel  niveau d’étage (détermine la difficulté)
     * @param exits       liste des directions pour les sorties disponibles
     */
    public Location(String name, String description, int floorLevel, List<Direction> exits) {
        this.name = name;
        this.description = description;
        this.floorLevel = floorLevel;
        this.difficulty = difficultyFromFloorLevel(floorLevel);
        this.exits = exits;

        this.loot = Consumable.getRandomConsumable(1);
        this.enemies = generateEnemies(difficulty, floorLevel);
    }

    /**
     * Construit une Location en spécifiant seulement l’étage et la description.
     * Le nom et les sorties par défaut sont générés automatiquement.
     *
     * @param floorLevel  numéro d’étage (définit nom et difficulté)
     * @param description description textuelle de l’emplacement
     */
    public Location(int floorLevel, String description) {
        this(floorLevel == 10 || floorLevel == 20 || floorLevel == 30 ? "Boss Floor" : "Floor " + floorLevel,
                description,
                floorLevel,
                Arrays.asList(Direction.values()));
    }

    /**
     * Retourne le nom de cette location.
     *
     * @return le nom de l’étage ou lieu
     */
    public String getName() {
        return this.name;
    }

    /**
     * Retourne la description textuelle de cette location.
     *
     * @return la description
     */
    public String getDescription() {
        return this.description;
    }

    /**
     * Retourne le numéro d’étage associé à cette location.
     *
     * @return le niveau d’étage (entier)
     */
    public int getFloorLevel() {
        return this.floorLevel;
    }

    /**
     * Retourne la liste des ennemis présents dans cette location.
     *
     * @return liste de Character (ennemis)
     */
    public List<Character> getEnemies() {
        return this.enemies;
    }

    /**
     * Retourne la difficulté de cette location, dérivée du numéro d’étage.
     *
     * @return la difficulté (EASY, NORMAL, HARD, IMPOSSIBLE)
     */
    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    /**
     * Retourne la liste des consommables (butin) générés pour cette location.
     *
     * @return liste de Consumable
     */
    public List<Consumable> getLoot() {
        return this.loot;
    }

    /**
     * Retourne les directions dans lesquelles on peut sortir de cette location.
     *
     * @return liste de Direction (copie de la liste interne)
     */
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

    /**
     * Génère automatiquement une nouvelle Location pour l’étage suivant,
     * avec un nombre aléatoire de sorties et un nom par défaut.
     *
     * @param from       direction d’où l’on arrive
     * @param floorLevel numéro d’étage courant
     * @return nouvelle Location pour l’étage floorLevel+1
     */
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

    /**
     * Affiche dans la console un message d’entrée dans la location.
     * Signale s’il s’agit d’un étage de boss ou non.
     */
    public void displayOnEnter() {
        if (floorLevel == 10 || floorLevel == 20 || floorLevel == 30) {
            System.out.println("⚔️ You are entering a BOSS floor!");
        } else {
            System.out.printf("You are on floor %d, difficulty: %s%n", floorLevel, difficulty);
        }
    }

    /**
     * Retourne la Location cible pour la direction donnée,
     * si elle a déjà été pré-générée.
     *
     * @param dir direction de sortie souhaitée
     * @return Location correspondante, ou null si non définie
     */
    public Location getNextLocation(Direction dir) {
        return nextFloors.get(dir);
    }
}
