package Vues.game;

import Controleurs.Menu.MenuControleur;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import utils.I18N;
import utils.TransitionUtils;

public class GameOverView extends StackPane {
    public GameOverView(Stage stage) {
        // Fond noir et centrage global
        this.setStyle("-fx-background-color: black;");
        this.setAlignment(Pos.CENTER);

        // Titre "Game Over"
        Label gameOverLabel = new Label(I18N.get("gameover.title"));
        gameOverLabel.setTextFill(Color.RED);
        gameOverLabel.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 80));

        // Bouton retour
        Button menuButton = new Button(I18N.get("gameover.backtomenu"));
        String baseStyle = """
            -fx-background-color: transparent;
            -fx-border-color: white;
            -fx-border-width: 3px;
            -fx-text-fill: white;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-padding: 10 30 10 30;
            -fx-font-family: 'Consolas';
            -fx-effect: dropshadow(one-pass-box, white, 2, 0, 0, 0);
        """;

        String hoverStyle = """
            -fx-background-color: white;
            -fx-text-fill: black;
            -fx-border-color: white;
            -fx-border-width: 3px;
            -fx-font-size: 20px;
            -fx-font-weight: bold;
            -fx-padding: 10 30 10 30;
            -fx-font-family: 'Consolas';
            -fx-effect: dropshadow(one-pass-box, white, 2, 0, 0, 0);
        """;

        menuButton.setStyle(baseStyle);
        menuButton.setOnMouseEntered(e -> menuButton.setStyle(hoverStyle));
        menuButton.setOnMouseExited(e -> menuButton.setStyle(baseStyle));

        menuButton.setOnAction(e -> {
            MenuControleur menuControleur = new MenuControleur(stage);
            TransitionUtils.fadeToScene(stage, menuControleur.creerVueAvecFond(new Vues.Menu.SplashMenu(menuControleur)));
        });

        // VBox centrée verticalement avec espacement
        VBox vbox = new VBox(40, gameOverLabel, menuButton);
        vbox.setAlignment(Pos.CENTER);

        // Ajout à la scène
        this.getChildren().add(vbox);

        // Forcer le redimensionnement sur toute la fenêtre
        this.prefWidthProperty().bind(stage.widthProperty());
        this.prefHeightProperty().bind(stage.heightProperty());
    }
}
