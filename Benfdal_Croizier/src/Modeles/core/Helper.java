package Modeles.core;

import Modeles.characters.Hero;
import Modeles.items.weapons.Weapon;
import Modeles.map.Direction;
import Modeles.map.Location;

public class Helper {

    private Helper() {}

    public static Hero askPlayerForWeapon() {
        Hero res = null;
        System.out.println("Choose a weapon: Shooter, Charger, or Roller");
        String weaponChoice = System.console().readLine();
    
        Weapon starterWeapon = Weapon.parseFromString(weaponChoice);
        System.out.println("What is your name?");
        String nameChoice = System.console().readLine();
        
        res = new Hero(nameChoice, 100, starterWeapon);
        return res;
    }

    public static void displayGameIntroduction(Hero hero) {
        System.out.println("The game begins! The hero " + hero.getName() + " starts at floor 1.");
    }

    public static void displayGameEnd(Hero hero) {
        if(hero.getHealth() > 0) {
            System.out.println("Congratulations ! You have completed all the floors");
        } else {
            System.out.println(String.format("Game over. %s is dead.", hero.getName()));
        }
    }

    public static void askPlayerForDirection(Location location) {
        System.out.println("Choose a level:");
        for(Direction dir : location.getExitDirections()) {
            // Display all loot ?
            String text = String.format("%d: go %s : %s", location.getFloorLevel(), dir.toString(), location.getLoot().get(0).toString());
            System.out.println(text);
        }
    }

    public static Command askPlayerForCommand() {
        Command res = null;
        System.out.println("Enter a command (HELP to see the options):");
        String input = System.console().readLine().trim();
        res = Command.FromString(input);
        return res;
    }



    public static void displayHelp() {
        StringBuilder builder = new StringBuilder();
        builder.append("Here are the available commands:\n");
        builder.append("GO [1|2|3] : Choose a floor to advance.\n");
        builder.append("HELP : Display this help.\n");
        builder.append("LOOK : Display your inventory.\n");
        builder.append("QUIT : Quit the game.\n");
        System.out.println(builder.toString());
    }

    public static void displayGameExit() {
        System.out.println("Exiting game ...");
    }

}
