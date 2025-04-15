package Modeles.game;

public class GameModel {
    private final boolean[][] grilleMarchable;
    private double playerX, playerY;
    private final int tailleCase = 64;

    public GameModel() {
        grilleMarchable = new boolean[17][30];

        // tout est marchable par défaut
        for (int y = 0; y < 17; y++) {
            for (int x = 0; x < 30; x++) {
                grilleMarchable[y][x] = true;
            }
        }


        // mur horizontal au-dessus
        for (int x = 8; x <= 20; x++) grilleMarchable[0][x] = false;

// murs verticaux gauche
        for (int y = 0; y <= 16; y++) grilleMarchable[y][8] = false;

// murs verticaux droite
        for (int y = 0; y <= 16; y++) grilleMarchable[y][20] = false;


        for (int y = 13; y <= 16; y++) {
            for (int x = 9; x <= 12; x++) {
                grilleMarchable[y][x] = false;
            }
        }

        for (int y = 13; y <= 16; y++) {
            for (int x = 16; x <= 19; x++) {
                grilleMarchable[y][x] = false;
            }
        }


        // position de spawn (ex: case 13,13)
        playerX = 13 * tailleCase;
        playerY = 13 * tailleCase;
    }

    public boolean peutAller(double futurX, double futurY) {
        double hitboxWidth = 64;  // largeur de la hitbox
        double hitboxHeight = 54;  // hauteur (épaisseur des pieds)

        double piedX = futurX + 64; // centre
        double piedY = futurY + 192; // bas

        int debutX = (int)((piedX - hitboxWidth / 2) / tailleCase);
        int finX = (int)((piedX + hitboxWidth / 2) / tailleCase);

        int debutY = (int)((piedY - hitboxHeight / 2) / tailleCase);
        int finY = (int)((piedY + hitboxHeight / 2) / tailleCase);

        for (int x = debutX; x <= finX; x++) {
            for (int y = debutY; y <= finY; y++) {
                if (!estMarchable(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean estMarchable(int x, int y) {
        return x >= 0 && x < grilleMarchable[0].length &&
                y >= 0 && y < grilleMarchable.length &&
                grilleMarchable[y][x];
    }

    public boolean estCaseBloquee(int x, int y) {
        return !estMarchable(x, y);
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
}
