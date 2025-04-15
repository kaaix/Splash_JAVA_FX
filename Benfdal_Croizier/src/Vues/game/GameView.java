package Vues.game;

import Modeles.game.GameModel;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameView extends StackPane {
    private final ImageView player;
    private final ImageView mapView;
    private final int nbCols = 30;
    private final int nbRows = 17;
    private final int taille = 64;
    private Rectangle hitbox;


    public GameView(GameModel model) {
        this.setBackground(new Background(new BackgroundFill(Color.BLACK, null, null)));

        Image mapImage = new Image(getClass().getResource("/assets/image/map.png").toExternalForm());
        mapView = new ImageView(mapImage);
        mapView.setPreserveRatio(false);
        mapView.setSmooth(false);
        mapView.fitHeightProperty().bind(heightProperty());
        mapView.fitWidthProperty().bind(widthProperty());

        Image playerImage = new Image(getClass().getResource("/assets/image/player.png").toExternalForm());
        player = new ImageView(playerImage);

        hitbox = new Rectangle(64, 64);
        hitbox.setStroke(Color.LIMEGREEN);
        hitbox.setFill(Color.color(0, 1, 0, 0.2)); // vert semi-transparent


        Pane mapLayer = new Pane(mapView);
        Pane playerLayer = new Pane();
        Pane grilleLayer = new Pane();
        grilleLayer.setMouseTransparent(true);

        playerLayer.getChildren().addAll(player, hitbox);


        // grille rouge avec remplissage pour les murs
        for (int y = 0; y < nbRows; y++) {
            for (int x = 0; x < nbCols; x++) {
                Rectangle r = new Rectangle(x * taille, y * taille, taille, taille);
                r.setStroke(Color.rgb(255, 0, 0, 0.3));
                if (model.estCaseBloquee(x, y)) {
                    r.setFill(Color.rgb(255, 0, 0, 0.3));
                } else {
                    r.setFill(Color.TRANSPARENT);
                }

                // Ajout du texte au centre
                Label coord = new Label(x + "," + y);
                coord.setTextFill(Color.rgb(255, 0, 0, 0.6));
                coord.setFont(Font.font("Consolas", FontWeight.BOLD, 12));
                coord.setLayoutX(x * taille + 4); // marge pour lisibilité
                coord.setLayoutY(y * taille + 4);

                grilleLayer.getChildren().addAll(r, coord);
            }
        }

        this.getChildren().addAll(mapLayer, grilleLayer, playerLayer);
        this.setFocusTraversable(true);
    }

    public void setPlayerPosition(double x, double y) {
        player.setLayoutX(x);
        player.setLayoutY(y);

        // positionner la hitbox en bas du sprite
        hitbox.setWidth(64);
        hitbox.setHeight(54);
        hitbox.setLayoutX(x + 64 - 8);  // centré
        hitbox.setLayoutY(y + 192 - 4); // aligné aux pieds


    }
}
