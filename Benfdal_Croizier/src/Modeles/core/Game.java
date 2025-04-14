package Modeles.core;

import java.util.List;

import Modeles.characters.Hero;
import Modeles.map.Direction;
import Modeles.map.Location;

public class Game {

    private static final int TOTAL_FLOOR = 30;
    
    private Hero hero;
    private int currentFloor = 0;

    public Game() {
        
    }

    public void run() {

        this.hero = Helper.askPlayerForWeapon();
        this.currentFloor = 0;

        Location loc = Location.generateNextFloor(Direction.SOUTH, this.currentFloor);
        while(isGameRunning()) {
            Helper.askPlayerForDirection(loc);
            Command command = Helper.askPlayerForCommand();
            switch(command.getType()) {
                case HELP: Helper.displayHelp(); break;
                case LOOK: this.hero.displayInventory(); break;
                case QUIT: Helper.displayGameExit(); System.exit(0);
                case GO: executeGoCommand(command, loc); break;
            }
        }
        Helper.displayGameEnd(this.hero);
    }

    private void executeGoCommand(Command command, Location location) {
        int ind = 0;
        if(command.length() < 1) {
            return;
        }
        try {
            ind = Integer.parseInt(command.get(0));
        } catch(NumberFormatException e) {
            System.out.println("Error parsing number");
            return;
        }
        List<Direction> exits = location.getExitDirections();
        int exitCount = exits.size();
        if(ind < 1) {
            System.out.println("Index too low.");
            return;
        }
        if(ind > exitCount) {
            System.out.println("Index too big.");
            return;
        }

        this.currentFloor++;
        Direction dir = exits.get(ind-1);
        Location nextLocation = Location.generateNextFloor(Direction.opposite(dir), this.currentFloor);
        this.hero.playFloor(nextLocation);
    }    

    private boolean isGameRunning() {
        return this.hero.getHealth() > 0 && currentFloor < TOTAL_FLOOR;
    }
    
}
