package Modeles.editor;

import javafx.geometry.Point2D;
import java.util.HashSet;
import java.util.Set;

public class LevelEditorModel {
    public static final int TILE_SIZE = 64;
    private final int cols, rows;
    private boolean[][] walkable;
    private Set<Point2D> nextFloorTiles = new HashSet<>();
    private Point2D playerSpawn;
    private Set<Point2D> enemySpawns = new HashSet<>();
    private String backgroundImagePath;
    private int floorNumber = 0;

    public LevelEditorModel(int cols, int rows) {
        this.cols = cols;
        this.rows = rows;
        walkable = new boolean[rows][cols];
        for (int y = 0; y < rows; y++)
            for (int x = 0; x < cols; x++)
                walkable[y][x] = true;
    }

    public int getCols() { return cols; }
    public int getRows() { return rows; }

    public void toggleWalkable(int x, int y) { walkable[y][x] = !walkable[y][x]; }
    public boolean isWalkable(int x, int y) { return walkable[y][x]; }

    public void addNextFloorTile(int x, int y) { nextFloorTiles.add(new Point2D(x, y)); }
    public void removeNextFloorTile(int x, int y) { nextFloorTiles.remove(new Point2D(x, y)); }
    public Set<Point2D> getNextFloorTiles() { return nextFloorTiles; }

    public void setPlayerSpawn(int x, int y) { playerSpawn = new Point2D(x, y); }
    public Point2D getPlayerSpawn() { return playerSpawn; }

    public void addEnemySpawn(int x, int y) { enemySpawns.add(new Point2D(x, y)); }
    public void removeEnemySpawn(int x, int y) { enemySpawns.remove(new Point2D(x, y)); }
    public Set<Point2D> getEnemySpawns() { return enemySpawns; }

    public String getBackgroundImagePath() { return backgroundImagePath; }
    public void setBackgroundImagePath(String path) { backgroundImagePath = path; }

    public int getFloorNumber() { return floorNumber; }
    public void setFloorNumber(int floorNumber) { this.floorNumber = floorNumber; }

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
