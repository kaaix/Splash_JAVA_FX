package Modeles.game;

import Modeles.characters.Hero;
import Modeles.map.Location;
import Modeles.map.Direction;

import java.util.List;

public class GameModel {

    private final boolean[][] grilleMarchable;
    private double playerX, playerY;
    private final int tailleCase = 64;
    private Hero hero;
    private int currentFloor = 0;
    private static final int TOTAL_FLOOR = 30;
    private Location locationActuelle;

    public GameModel() {
        grilleMarchable = new boolean[17][30];

        // Initialisation de la grille
        for (int y = 0; y < 17; y++) {
            for (int x = 0; x < 30; x++) {
                grilleMarchable[y][x] = true;
            }
        }

        this.locationActuelle = new Location(1, "Début du jeu");

        // Exemple de murs
        for (int x = 8; x <= 20; x++) grilleMarchable[3][x] = false;
        for (int y = 0; y <= 16; y++) grilleMarchable[y][8] = false;
        for (int y = 0; y <= 16; y++) grilleMarchable[y][20] = false;

        // Position initiale du joueur
        playerX = 13 * tailleCase;
        playerY = 13 * tailleCase;
    }

    public boolean peutAller(double futurX, double futurY) {
        double spriteWidth = 150;
        double spriteHeight = 150;

        double piedX = futurX + spriteWidth / 2;
        double piedY = futurY + spriteHeight - 10;

        int caseX = (int)(piedX / tailleCase);
        int caseY = (int)(piedY / tailleCase);

        return estMarchable(caseX, caseY);
    }

    private boolean estMarchable(int x, int y) {
        return x >= 0 && x < grilleMarchable[0].length &&
                y >= 0 && y < grilleMarchable.length &&
                grilleMarchable[y][x];
    }

    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public int getTailleCase() {
        return tailleCase;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }


    // Méthode pour déplacer le joueur
    public void movePlayer(double newX, double newY) {
        if (peutAller(newX, newY)) {
            setPlayerPosition(newX, newY);
            if (checkNextFloor()) {
                // Changer d'étage et afficher la vue de choix
                changerEtage(currentFloor + 1, "Nouvelle zone !");
            }
        }
    }

    public boolean estCaseBloquee(int x, int y) {
        // Retourne si la case (x, y) est bloquée dans la grille
        return x >= 0 && x < grilleMarchable[0].length &&
                y >= 0 && y < grilleMarchable.length &&
                !grilleMarchable[y][x]; // Retourne true si la case est bloquée (mur)
    }

    // Vérifie si le joueur se trouve sur l'une des cases pour avancer à l'étage suivant
    public boolean checkNextFloor() {
        double spriteWidth = 150;
        double hitboxWidth = 16;
        double hitboxHeight = 10;
        double spriteHeight = 150;

        // Centre bas du personnage = centre de la hitbox
        double hitboxX = playerX + (spriteWidth - hitboxWidth) / 2;
        double hitboxY = playerY + spriteHeight - hitboxHeight - 10;

        int x = (int) (hitboxX / tailleCase);
        int y = (int) (hitboxY / tailleCase);

        return (x == 13 && y == 4) || (x == 14 && y == 4) || (x == 15 && y == 4);
    }


    public Location getLocationActuelle() {
        return locationActuelle;
    }

    public void changerEtage(int nouvelÉtage, String nouvelleDescription) {
        this.locationActuelle = new Location(nouvelÉtage, nouvelleDescription);
    }
}
