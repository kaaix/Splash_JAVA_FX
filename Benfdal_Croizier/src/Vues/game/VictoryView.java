/**
 * Vue affichée lorsque le joueur remporte une partie.
 * Affiche un message de félicitations, le nom et le score du joueur,
 * sauvegarde automatiquement le score, propose un bouton de retour
 * au menu principal et affiche le Top 5 des meilleurs scores.
 */
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
import utils.ScoreManager;
import utils.TransitionUtils;

import java.util.List;

public class VictoryView extends StackPane {
    /**
     * Construit la vue de victoire.
     *
     * @param stage         la fenêtre principale de l’application
     * @param playerName    le nom du joueur ayant gagné
     * @param scoreSeconds  le score obtenu (en secondes)
     */
    public VictoryView(Stage stage, String playerName, int scoreSeconds) {
        this.setStyle("-fx-background-color: black;");
        this.setAlignment(Pos.CENTER);

        Label victoryLabel = new Label(I18N.get("victory.title"));
        victoryLabel.setTextFill(Color.WHITE);
        victoryLabel.setFont(Font.font("Consolas", FontWeight.EXTRA_BOLD, 70));

        // Score du joueur
        Label yourScoreLabel = new Label("\uD83C\uDF89 " + playerName + " - " + scoreSeconds + " sec");
        yourScoreLabel.setTextFill(Color.LIGHTGREEN);
        yourScoreLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));

        // Sauvegarde automatique du score
        ScoreManager.saveScore(playerName, scoreSeconds);

        // Bouton retour au menu
        Button menuButton = new Button(I18N.get("victory.backtomenu"));
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

        // Top 5
        VBox topScoresBox = new VBox(5);
        topScoresBox.setAlignment(Pos.CENTER);
        Label topLabel = new Label("\uD83C\uDFC6 Top 5");
        topLabel.setTextFill(Color.GOLD);
        topLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        topScoresBox.getChildren().add(topLabel);

        List<String> top5 = ScoreManager.getTop5();
        int rank = 1;
        for (String score : top5) {
            Label scoreLabel = new Label(rank + ". " + score);
            scoreLabel.setTextFill(Color.WHITE);
            scoreLabel.setFont(Font.font("Arial", FontWeight.NORMAL, 16));
            topScoresBox.getChildren().add(scoreLabel);
            rank++;
        }

        VBox vbox = new VBox(40, victoryLabel, yourScoreLabel, menuButton, topScoresBox);
        vbox.setAlignment(Pos.CENTER);
        this.getChildren().add(vbox);

        this.prefWidthProperty().bind(stage.widthProperty());
        this.prefHeightProperty().bind(stage.heightProperty());
    }
}
