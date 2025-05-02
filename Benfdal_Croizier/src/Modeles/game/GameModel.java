/**
 * Modèle représentant l’état du jeu.
 * Contient la grille de déplacement, la position du joueur,
 * l’étage courant, la location associée et le chronomètre.
 */
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
    private long startTime = -1;

    /**
     * Initialise un nouveau GameModel :
     * – grille 17×30 praticable par défaut (avec quelques murs d’exemple)
     * – positionne le joueur et la Location initiale
     * – configure la taille des cases et le chronomètre
     */
    public GameModel() {
        grilleMarchable = new boolean[17][30];

        // Initialisation de la grille
        for (int y = 0; y < 17; y++) {
            for (int x = 0; x < 30; x++) {
                grilleMarchable[y][x] = true;
            }
        }

        this.locationActuelle = new Location("Début", "Début du jeu", 1, List.of(Direction.NORTH));


        // Exemple de murs
        for (int x = 8; x <= 20; x++) grilleMarchable[3][x] = false;
        for (int y = 0; y <= 16; y++) grilleMarchable[y][8] = false;
        for (int y = 0; y <= 16; y++) grilleMarchable[y][20] = false;


        for (int x = 14; x <= 16; x++){
        for (int y = 9; y <= 12; y++) {
            grilleMarchable[x][y] = false;
        }}

        for (int x = 14; x <= 16; x++){
            for (int y = 16; y <= 19; y++) {
                grilleMarchable[x][y] = false;
            }}




        // Position initiale du joueur
        playerX = 13 * tailleCase;
        playerY = 13 * tailleCase;
    }

    /**
     * Vérifie si le joueur peut se déplacer vers la position future
     * en testant la case sous sa hitbox.
     *
     * @param futurX coordonnée X future du joueur (pixels)
     * @param futurY coordonnée Y future du joueur (pixels)
     * @return true si la case est praticable, false sinon
     */
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

    /**
     * Met à jour la position du joueur dans le modèle.
     *
     * @param x nouvelle abscisse du joueur (pixels)
     * @param y nouvelle ordonnée du joueur (pixels)
     */
    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }

    /**
     * Retourne la coordonnée X actuelle du joueur.
     *
     * @return position X en pixels
     */
    public double getPlayerX() {
        return playerX;
    }

    /**
     * Retourne la coordonnée Y actuelle du joueur.
     *
     * @return position Y en pixels
     */
    public double getPlayerY() {
        return playerY;
    }

    /**
     * Retourne la taille, en pixels, d’une case de la grille.
     *
     * @return taille fixe de chaque case (ici 64)
     */
    public int getTailleCase() {
        return tailleCase;
    }

    /**
     * Retourne l’instance du héros associée au modèle.
     *
     * @return objet Hero courant
     */
    public Hero getHero() {
        return hero;
    }

    /**
     * Associe un objet Hero au modèle de jeu.
     *
     * @param hero instance du héros à utiliser
     */
    public void setHero(Hero hero) {
        this.hero = hero;
    }


    /**
     * Tente de déplacer le joueur aux coordonnées spécifiées.
     * Si la case est praticable, met à jour la position ;
     * si le joueur atteint la sortie, passe à l’étage suivant.
     *
     * @param newX nouvelle abscisse du joueur (pixels)
     * @param newY nouvelle ordonnée du joueur (pixels)
     */
    public void movePlayer(double newX, double newY) {
        if (peutAller(newX, newY)) {
            setPlayerPosition(newX, newY);
            if (checkNextFloor()) {
                // Changer d'étage et afficher la vue de choix
                changerEtage(currentFloor + 1, "Nouvelle zone !");
            }
        }
    }

    // 11. Avant estCaseBloquee
    /**
     * Indique si la case donnée est bloquée (mur).
     *
     * @param x indice de colonne de la case
     * @param y indice de ligne de la case
     * @return true si la case n’est pas praticable, false sinon
     */
    public boolean estCaseBloquee(int x, int y) {
        // Retourne si la case (x, y) est bloquée dans la grille
        return x >= 0 && x < grilleMarchable[0].length &&
                y >= 0 && y < grilleMarchable.length &&
                !grilleMarchable[y][x]; // Retourne true si la case est bloquée (mur)
    }

    /**
     * Vérifie si le joueur se trouve sur une case de transition
     * vers l’étage suivant.
     *
     * @return true si le joueur peut monter d’étage
     */
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

    /**
     * Retourne la Location correspondant à l’étage courant.
     *
     * @return locationActuelle du modèle
     */
    public Location getLocationActuelle() {
        return locationActuelle;
    }

    /**
     * Change l’étage courant et crée une nouvelle Location
     * pour représenter cet étage avec sa description.
     *
     * @param nouvelÉtage       numéro du nouvel étage
     * @param nouvelleDescription description textuelle associée
     */
    public void changerEtage(int nouvelÉtage, String nouvelleDescription) {
        this.currentFloor = nouvelÉtage; // ✅ MET À JOUR L'ÉTAGE COURANT
        this.locationActuelle = new Location("Etage " + nouvelÉtage, nouvelleDescription, nouvelÉtage, List.of(Direction.NORTH));
    }

    /**
     * Démarre le chronomètre si ce n’est pas déjà fait.
     * Permet de mesurer le temps de jeu écoulé.
     */
    public void demarrerChrono() {
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        }
    }

    /**
     * Retourne le temps écoulé depuis le démarrage (en secondes).
     *
     * @return durée écoulée (secondes)
     */
    public int getTempsEnSecondes() {
        if (startTime == -1) return 0;
        return (int) ((System.currentTimeMillis() - startTime) / 1000);
    }

    /**
     * Définit un décalage pour le chronomètre, utile lors
     * du chargement d’une sauvegarde pour conserver le temps.
     *
     * @param seconds nombre de secondes à retrancher au chrono
     */
    public void setChronoOffset(int seconds) {
        startTime = System.currentTimeMillis() - seconds * 1000L;
    }

}
