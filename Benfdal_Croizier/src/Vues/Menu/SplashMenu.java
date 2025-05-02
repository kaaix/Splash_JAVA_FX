package Vues.Menu;

import Controleurs.Menu.MenuControleur;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;
import utils.I18N; // ← AJOUT I18N

import java.io.File;

public class SplashMenu extends VBox {

    public SplashMenu(MenuControleur controleur) {
        this.setSpacing(20);
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
        VBox selectionBox = new VBox(10);
        selectionBox.setAlignment(Pos.CENTER_LEFT);
        selectionBox.setVisible(false); // caché au départ

        // === Boutons ===
        Button play = createStyledButton(I18N.get("splash.play"));
        Button settings = createStyledButton(I18N.get("splash.settings"));
        Button quitter = createStyledButton(I18N.get("splash.quit"));

        Button levelEditor = createStyledButton(I18N.get("splash.level_editor"));
        levelEditor.setOnAction(e -> {
            controleur.ouvrirEditeurNiveau();
        });


        settings.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/settingssfx.wav");
            controleur.ouvrirSettings();
        });

        quitter.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/quit.wav");
            utils.MusicPlayer.fadeOutAndStop(1.5);
            utils.TransitionUtils.fadeToBlackAndExit(controleur.getStage());
        });

        VBox playButtonBox = new VBox(play);
        playButtonBox.setAlignment(Pos.CENTER_LEFT);


        play.setOnAction(e -> {
            utils.SoundEffects.play("Benfdal_Croizier/src/assets/audio/play.wav");
            controleur.ouvrirSelectionSauvegarde();
        });

        this.getChildren().addAll(play, settings, levelEditor,quitter);

    }

    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 20));
        button.setTextFill(Color.web("#333333"));
        button.setStyle("-fx-background-color: white;-fx-background-radius: 20;-fx-padding: 10 30;-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #dddddd;-fx-background-radius: 20;-fx-padding: 10 30;-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: white;-fx-background-radius: 20;-fx-padding: 10 30;-fx-cursor: hand;"));
        return button;
    }

}
