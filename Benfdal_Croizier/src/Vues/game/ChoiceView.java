package Vues.game;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import Controleurs.Game.GameControleur;

public class ChoiceView {

    private final Stage stage;
    private final GameControleur gameController;  // Utiliser le contrôleur du jeu

    public ChoiceView(Stage stage, GameControleur gameController) {
        this.stage = stage;
        this.gameController = gameController;  // Référence au contrôleur
    }

    public void showChoiceScene() {
        StackPane choicePane = new StackPane();

        // Charger l'image pour l'écran de choix
        ImageView choiceImage = new ImageView(new Image(getClass().getResource("/assets/image/choix.png").toExternalForm()));
        choiceImage.setFitWidth(800);
        choiceImage.setFitHeight(600);

        choicePane.getChildren().add(choiceImage);

        // Créer un bouton pour passer à l'étage suivant
        Button choiceButton = new Button("Choisir l'étage suivant");
        choiceButton.setOnAction(event -> {
            gameController.updateFloor();  // Mettre à jour l'étage

            // Assurez-vous que la vue n'est pas null avant de la récupérer
            Scene gameScene = new Scene(gameController.getVue());  // Assurez-vous que getVue() renvoie une vue valide
            stage.setScene(gameScene);  // Revenir à la scène du jeu
        });

        choicePane.getChildren().add(choiceButton);
        StackPane.setAlignment(choiceButton, Pos.BOTTOM_CENTER);

        // Créer et afficher la scène de choix
        Scene choiceScene = new Scene(choicePane, 800, 600);
        stage.setScene(choiceScene);
    }
}
