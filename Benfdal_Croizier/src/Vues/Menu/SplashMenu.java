package Vues.Menu;

import Controleurs.Menu.MenuControleur;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;

import java.io.File;

public class SplashMenu extends VBox {

    public SplashMenu(MenuControleur controleur) {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        controleur.jouerMusiqueMenu(); // propre, centré dans le contrôleur

        // === Logo ===
        File logoFile = new File("Benfdal_Croizier/src/assets/image/logo.png");
        if (logoFile.exists()) {
            ImageView logo = new ImageView(new Image(logoFile.toURI().toString()));
            logo.setFitHeight(200);
            logo.setPreserveRatio(true);
            this.getChildren().add(logo);
        } else {
            System.out.println("❌ Logo non trouvé !");
        }

        // === Boutons ===
        Button play = createStyledButton("▶ PLAY");
        Button settings = createStyledButton("⚙ SETTINGS");
        Button quitter = createStyledButton("❌ QUITTER");

        // Actions
        play.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/play.wav");
            controleur.lancerJeu();
        });

        settings.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/settingssfx.wav");
            controleur.ouvrirSettings();
        });

        quitter.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/quit.wav"); // facultatif
            utils.MusicPlayer.fadeOutAndStop(1.5); // fondu audio
            utils.TransitionUtils.fadeToBlackAndExit(controleur.getStage()); // fondu visuel + Platform.exit()
        });


        this.getChildren().addAll(play, settings, quitter);
    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 20));
        button.setTextFill(Color.web("#333333"));
        button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 30;" +
                        "-fx-cursor: hand;"
        );

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: #dddddd;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 30;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: white;" +
                        "-fx-background-radius: 20;" +
                        "-fx-padding: 10 30;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }
}
