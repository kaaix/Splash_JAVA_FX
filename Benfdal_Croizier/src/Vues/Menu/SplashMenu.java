package Vues.Menu;

import Controleurs.Menu.MenuControleur;
import javafx.animation.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;

public class SplashMenu extends Application {

    private MenuControleur controleur;

    @Override
    public void start(Stage primaryStage) {
        controleur = new MenuControleur(this, primaryStage); // Associe la vue au contrôleur

        // Vérifier si les fichiers existent avant de les charger
        File bgFile = new File("src/image/menu_background.png");
        File logoFile = new File("src/image/logo.png");

        if (!bgFile.exists() || !logoFile.exists()) {
            System.out.println("❌ ERREUR: Images introuvables !");
            return;
        }

        // Charger le fond d'écran et le logo
        ImageView background = new ImageView(new Image(bgFile.toURI().toString()));
        background.setFitWidth(800);
        background.setFitHeight(600);

        ImageView logo = new ImageView(new Image(logoFile.toURI().toString()));
        logo.setFitWidth(400);
        logo.setFitHeight(200);
        logo.setTranslateY(-50);

        // Animation du logo
        TranslateTransition fall = new TranslateTransition(Duration.seconds(1), logo);
        fall.setFromY(-300);
        fall.setToY(0);
        fall.setInterpolator(Interpolator.EASE_OUT);

        ScaleTransition bounce = new ScaleTransition(Duration.seconds(0.3), logo);
        bounce.setFromX(1);
        bounce.setFromY(1);
        bounce.setToX(1.1);
        bounce.setToY(1.1);
        bounce.setAutoReverse(true);
        bounce.setCycleCount(2);

        SequentialTransition logoAnimation = new SequentialTransition(fall, bounce);

        // Boutons
        Button playButton = new Button("Play");
        Button settingsButton = new Button("Paramètres");

        playButton.setStyle("-fx-font-size: 24px; -fx-background-color: #ff6600; -fx-text-fill: white;");
        settingsButton.setStyle("-fx-font-size: 24px; -fx-background-color: #444; -fx-text-fill: white;");

        // Actions des boutons gérées par le contrôleur
        playButton.setOnAction(e -> controleur.lancerJeu());
        settingsButton.setOnAction(e -> controleur.ouvrirParametres());

        // Mise en page
        VBox menu = new VBox(20, playButton, settingsButton);
        menu.setTranslateY(100);

        StackPane root = new StackPane(background, logo, menu);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Splash Menu");
        primaryStage.show();

        // Lancer les animations
        logoAnimation.play();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
