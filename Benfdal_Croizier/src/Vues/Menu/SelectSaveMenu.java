/**
 * Vue de sélection ou création de sauvegardes.
 * Affiche jusqu’à 3 emplacements de sauvegarde avec
 * un bouton “jouer” ou “nouvelle partie” et un bouton
 * “supprimer”. Propose également un retour au menu principal.
 */
package Vues.Menu;

import Controleurs.Menu.MenuControleur;
import Modeles.save.SaveData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.effect.DropShadow;
import utils.I18N;

import java.io.File;

public class SelectSaveMenu extends VBox {

    /**
     * Construit la vue de sélection de sauvegardes.
     * Pour chaque slot (1 à 3), affiche un bouton pour
     * lancer la partie existante ou en créer une nouvelle,
     * ainsi qu’un bouton pour supprimer la sauvegarde.
     *
     * @param controleur le contrôleur de menu permettant
     *                   de lancer ou supprimer les parties
     */
    public SelectSaveMenu(MenuControleur controleur) {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(40));

        for (int i = 1; i <= 3; i++) {
            String nomFichier = "save" + i + ".bin";
            File fichier = new File(nomFichier);

            Button jouer;
            Button supprimer = createStyledButton(I18N.get("selectsave.delete"));

            if (!fichier.exists()) {
                jouer = createStyledButton(I18N.get("selectsave.new"));
                jouer.setOnAction(e -> {
                    controleur.lancerNouvellePartie(nomFichier);
                });
                supprimer.setDisable(true);
            } else {
                SaveData data = controleur.chargerSave(nomFichier);
                String nomHero = "???";
                int secondes = 0;
                if (data != null) {
                    nomHero = data.nomHero != null ? data.nomHero : "???";
                    secondes = data.timeSeconds;
                }
                assert data != null;
                jouer = createStyledButton("▶ " + nomHero + " - " + I18N.get("selectsave.floor") + " " + data.floorLevel);



                jouer.setOnAction(e -> controleur.lancerJeuDepuisSave(nomFichier));
                supprimer.setDisable(false);
                supprimer.setOnAction(e -> {
                    if (fichier.delete()) {
                        jouer.setDisable(false);
                        jouer.setText(I18N.get("selectsave.new"));
                        jouer.setOnAction(ev -> controleur.lancerNouvellePartie(nomFichier));
                        HBox parentBox = (HBox) supprimer.getParent();
                        parentBox.getChildren().remove(supprimer);
                    }
                });
            }


            HBox ligne;
            if (fichier.exists()) {
                ligne = new HBox(20, jouer, supprimer);
            } else {
                ligne = new HBox(20, jouer);
            }
            ligne.setAlignment(Pos.CENTER);
            this.getChildren().add(ligne);
        }

        Button retour = createStyledButton(I18N.get("selectsave.back"));

        retour.setOnAction(e -> {
            SplashMenu menu = new SplashMenu(controleur);
            StackPane root = controleur.creerVueAvecFond(menu);
            controleur.getStage().setFullScreenExitHint("");
            controleur.getStage().setFullScreen(controleur.getSettingsModel().isFullscreen());
            utils.TransitionUtils.fadeToScene(controleur.getStage(), root);
        });

        this.getChildren().add(retour);
    }

    /**
     * Crée un bouton au style uniforme pour ce menu.
     * Applique police, couleur de fond, arrondis et ombre.
     *
     * @param text le libellé à afficher sur le bouton
     * @return un Button JavaFX configuré avec effets visuels
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Segoe UI Emoji", 18));
        button.setTextFill(Color.web("#333333"));
        button.setStyle("-fx-background-color: white;-fx-background-radius: 20;-fx-padding: 10 25;-fx-cursor: hand;");

        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(3.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #dddddd;-fx-background-radius: 20;-fx-padding: 10 25;-fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: white;-fx-background-radius: 20;-fx-padding: 10 25;-fx-cursor: hand;"));
        return button;
    }
}