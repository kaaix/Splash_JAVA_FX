package Modeles.game;

public class GameModel {
    private double playerX;
    private double playerY;

    public GameModel(double initialX, double initialY) {
        this.playerX = initialX;
        this.playerY = initialY;
    }

    public GameModel() {
        this(0, 0); // par défaut
    }

    public double getPlayerX() {
        return playerX;
    }

    public double getPlayerY() {
        return playerY;
    }

    public void moveUp() {
        playerY -= 10;
    }

    public void moveDown() {
        playerY += 10;
    }

    public void moveLeft() {
        playerX -= 10;
    }

    public void moveRight() {
        playerX += 10;
    }

    public void setPlayerPosition(double x, double y) {
        this.playerX = x;
        this.playerY = y;
    }
}
