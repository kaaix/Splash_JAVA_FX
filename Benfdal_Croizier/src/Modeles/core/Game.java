package Modeles.core;

import java.util.List;
import Modeles.characters.Hero;
import Modeles.map.Direction;
import Modeles.map.Location;

public class Game {

    private static final int TOTAL_FLOOR = 30;
    private Hero hero;
    private int currentFloor = 0;

    public Game() {}

    public void run() {
        this.hero = Helper.askPlayerForWeapon();
        this.currentFloor = 0;

        Location loc = Location.generateNextFloor(Direction.SOUTH, this.currentFloor);
        loc.generateNextFloors(this.currentFloor); // Pré-génère les salles voisines

        while (isGameRunning()) {
            Helper.askPlayerForDirection(loc);
            Command command = Helper.askPlayerForCommand();

            switch (command.getType()) {
                case HELP: Helper.displayHelp(); break;
                case LOOK: this.hero.displayInventory(); break;
                case QUIT: Helper.displayGameExit(); System.exit(0); break;
                case GO:
                    loc = executeGoCommand(command, loc); // 🆕 mise à jour de la salle actuelle
                    break;
            }
        }

        Helper.displayGameEnd(this.hero);
    }

    private Location executeGoCommand(Command command, Location location) {
        if (command.length() < 1) return location;

        int ind;
        try {
            ind = Integer.parseInt(command.get(0));
        } catch (NumberFormatException e) {
            System.out.println("Error parsing number");
            return location;
        }

        List<Direction> exits = location.getExitDirections();
        
        if (ind < 1 || ind > exits.size()) {
            System.out.println("Index out of bounds.");
            return location;
        }

        this.currentFloor++;
        Direction dir = exits.get(ind - 1);
        Location nextLocation = location.getNextLocation(dir);      // récupère la salle déjà générée
        nextLocation.generateNextFloors(this.currentFloor);         // pré-génère les suivantes
        this.hero.playFloor(nextLocation);
        return nextLocation; // 🆕 retourne la salle pour mise à jour
    }

    private boolean isGameRunning() {
        return this.hero.getHealth() > 0 && currentFloor < TOTAL_FLOOR;
    }
}
