/**
 * Vue principale du menu de démarrage (Splash Menu) de l'application.
 * Affiche le logo, les boutons de navigation (Play, Settings, Level Editor, Quitter)
 * et délègue les actions utilisateur au contrôleur MenuControleur associé.
 */
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

    /**
     * Construit la vue du Splash Menu et initialise les composants graphiques.
     *
     * @param controleur instance du contrôleur de menu, utilisé pour gérer les actions utilisateur
     */

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


    /**
     * Crée un bouton stylisé pour le menu avec ombre portée et changements de style au survol.
     *
     * @param text le texte à afficher sur le bouton
     * @return un nouvel objet Button configuré
     */
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
