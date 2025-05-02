/**
 * Modèle de l’éditeur de niveaux 2D.
 * Gère la grille de tiles (marchables ou non), le placement
 * des tuiles “next floor”, le point de spawn du joueur,
 * les spawns ennemis, l’image de fond et le numéro d’étage.
 */
package Modeles.editor;

import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.Set;

public class LevelEditorModel {
    /** Taille en pixels d’une tuile dans l’éditeur (largeur = hauteur). */
    public static final int TILE_SIZE = 64;
    private final int cols, rows;
    private boolean[][] walkable;
    private Set<Point2D> nextFloorTiles = new HashSet<>();
    private Point2D playerSpawn;
    private Set<Point2D> enemySpawns = new HashSet<>();
    private String backgroundImagePath;
    private int floorNumber = 0;

    /**
     * Initialise un nouveau modèle d’éditeur de niveaux.
     * Par défaut, toutes les cases sont marchables.
     *
     * @param cols nombre de colonnes de la grille
     * @param rows nombre de lignes de la grille
     */
    public LevelEditorModel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        walkable = new boolean[rows][cols];
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                walkable[y][x] = true;
    }

    /**
     * Retourne le nombre de colonnes (resp. de lignes) de la grille.
     *
     * @return nombre de colonnes (getCols) ou de lignes (getRows)
     */
    public int getCols() { return cols; }
    public int getRows() { return rows; }

    /**
     * Inverse l’état “marchable” de la tuile spécifiée.
     *
     * @param x indice de colonne
     * @param y indice de ligne
     */
    public void toggleWalkable(int x, int y) { walkable[y][x] = !walkable[y][x]; }

    /**
     * Indique si la tuile spécifiée est marchable.
     *
     * @param x indice de colonne
     * @param y indice de ligne
     * @return true si la tuile est marchable, false sinon
     */
    public boolean isWalkable(int x, int y) { return walkable[y][x]; }

    /**
     * Gère l’ensemble des positions de la tuile “étage suivant”.
     * on peut ajouter, retirer ou récupérer la liste de ces positions.
     *
     * @param x indice de colonne de la tuile
     * @param y indice de ligne de la tuile
     */
    public void addNextFloorTile(int x, int y) { nextFloorTiles.add(new Point2D(x, y)); }
    public void removeNextFloorTile(int x, int y) { nextFloorTiles.remove(new Point2D(x, y)); }

    /**
     * Retourne les coordonnées de toutes les tuiles “next floor”.
     * @return Set de Point2D correspondant
     */
    public Set<Point2D> getNextFloorTiles() { return nextFloorTiles; }

    /**
     * Définit ou récupère le point de spawn du joueur.
     *
     * @param x indice de colonne du spawn
     * @param y indice de ligne du spawn
     * @return Point2D du spawn (null si non défini)
     */
    public void setPlayerSpawn(int x, int y) { playerSpawn = new Point2D(x, y); }
    public Point2D getPlayerSpawn() { return playerSpawn; }

    /**
     * Gère les positions de spawn des ennemis.
     * Permet d’ajouter, retirer ou récupérer la liste.
     *
     * @param x indice de colonne du spawn ennemi
     * @param y indice de ligne du spawn ennemi
     */
    public void addEnemySpawn(int x, int y) { enemySpawns.add(new Point2D(x, y)); }
    public void removeEnemySpawn(int x, int y) { enemySpawns.remove(new Point2D(x, y)); }

    /**
     * Retourne toutes les positions de spawn ennemis.
     * @return Set de Point2D
     */
    public Set<Point2D> getEnemySpawns() { return enemySpawns; }

    /**
     * Retourne ou définit le chemin de l’image de fond du niveau.
     *
     * @return chemin vers le fichier image (String)
     */
    public String getBackgroundImagePath() { return backgroundImagePath; }
    public void setBackgroundImagePath(String path) { backgroundImagePath = path; }

    /**
     * Retourne ou définit le numéro d’étage courant du niveau.
     *
     * @return entier floorNumber
     */
    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

    /**
     * Réinitialise entièrement le modèle :
     * rend toutes les tuiles marchables, supprime
     * spawns et fond, et remet l’étage à zéro.
     */
    public void clear() {
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                walkable[y][x] = true;
        nextFloorTiles.clear();
        enemySpawns.clear();
        playerSpawn = null;
        backgroundImagePath = null;
        floorNumber = 0;
    }
}
