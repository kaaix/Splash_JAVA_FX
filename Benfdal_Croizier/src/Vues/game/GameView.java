package Vues.game;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Bounds;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class GameView extends StackPane {
    private final ImageView player;
    private final ImageView mapView;
    private final Pane obstacleLayer;
    private final List<Rectangle> obstacles = new ArrayList<>();

    public GameView() {
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        // Map
        Image mapImage = new Image(getClass().getResource("/assets/image/map.png").toExternalForm());
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(true);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());

        StackPane mapContainer = new StackPane(mapView);
        mapContainer.setAlignment(Pos.CENTER);

        // Joueur
        Image playerImage = new Image(getClass().getResource("/assets/image/player.png").toExternalForm());
        player = new ImageView(playerImage);

        Pane playerLayer = new Pane();
        playerLayer.setPickOnBounds(false);
        playerLayer.prefWidthProperty().bind(widthProperty());
        playerLayer.prefHeightProperty().bind(heightProperty());
        playerLayer.getChildren().add(player);

        // Couche pour les obstacles
        obstacleLayer = new Pane();
        obstacleLayer.setPickOnBounds(false);
        obstacleLayer.prefWidthProperty().bind(widthProperty());
        obstacleLayer.prefHeightProperty().bind(heightProperty());

        // Empilement final
        this.getChildren().addAll(mapContainer, obstacleLayer, playerLayer);
        this.setFocusTraversable(true);
    }

    public void setPlayerPosition(double x, double y) {
        player.setLayoutX(x);
        player.setLayoutY(y);
    }

    public double getPlayerX() {
        return player.getLayoutX();
    }

    public double getPlayerY() {
        return player.getLayoutY();
    }

    public void centrerPlayerEnBas(Runnable onCentered) {
        ChangeListener<Number> listener = new ChangeListener<>() {
            @Override
            public void changed(ObservableValue<? extends Number> obs, Number oldVal, Number newVal) {
                if (getWidth() > 0 && getHeight() > 0) {
                    double playerX = (getWidth() - player.getImage().getWidth()) / 2;
                    double playerY = getHeight() - player.getImage().getHeight() - 10;
                    setPlayerPosition(playerX, playerY);

                    widthProperty().removeListener(this);
                    heightProperty().removeListener(this);

                    if (onCentered != null) onCentered.run();
                }
            }
        };
        widthProperty().addListener(listener);
        heightProperty().addListener(listener);
    }

    public void ajouterObstacle(Rectangle obstacle) {
        obstacle.setFill(Color.rgb(255, 0, 0, 0.4)); // Debug visuel : rouge semi-transparent
        obstacleLayer.getChildren().add(obstacle);
        obstacles.add(obstacle);
    }

    public boolean detecteCollision(double futurX, double futurY) {
        player.setLayoutX(futurX);
        player.setLayoutY(futurY);
        Bounds playerBounds = player.getBoundsInParent();

        for (Rectangle obstacle : obstacles) {
            if (playerBounds.intersects(obstacle.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
}
