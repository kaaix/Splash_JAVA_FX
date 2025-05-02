/**
 * Vue des paramètres généraux de l'application.
 * Permet de configurer le volume audio, le plein écran, la résolution,
 * la langue et les touches de contrôle.
 */
package Vues.Menu;

import Controleurs.Menu.SettingsControleur;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Duration;
import utils.I18N;
import utils.MusicPlayer;
import utils.SoundEffects;

import java.util.Locale;


public class SettingsView extends StackPane {
    private final SettingsControleur controleur;
    private final StackPane overlayPane = new StackPane();
    private final HBox resolutionBox;

    /**
     * Construit la vue des paramètres et initie les liaisons avec le contrôleur.
     *
     * @param controleur le contrôleur gérant la logique et la persistance des paramètres
     */
    public SettingsView(SettingsControleur controleur) {
        this.controleur = controleur;

        VBox contentBox = new VBox();
        contentBox.setSpacing(30);
        contentBox.setAlignment(Pos.CENTER);
        contentBox.setPadding(new Insets(30));
        contentBox.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 20;");
        contentBox.maxWidthProperty().bind(controleur.getStage().widthProperty().multiply(0.5));
        contentBox.maxHeightProperty().bind(controleur.getStage().heightProperty().multiply(0.9));
        contentBox.setMinWidth(500);

        Label titre = new Label(I18N.get("settings.title"));
        titre.setFont(Font.font("Orbitron", 28));
        titre.setTextFill(Color.WHITE);

        resolutionBox = createResolutionBox();

        // Volume musique
        Label musicLabel = new Label(I18N.get("music.volume"));
        musicLabel.setFont(Font.font("Arial", 16));
        musicLabel.setTextFill(Color.WHITE);
        Slider musicSlider = new Slider(0, 100, controleur.getMusicVolume());
        musicSlider.setPrefWidth(300);
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            controleur.setMusicVolume(value);
            MusicPlayer.setVolume(value / 100.0);
        });

        // Volume SFX
        Label sfxLabel = new Label(I18N.get("sfx.volume"));
        sfxLabel.setFont(Font.font("Arial", 16));
        sfxLabel.setTextFill(Color.WHITE);
        Slider sfxSlider = new Slider(0, 100, controleur.getSfxVolume());
        sfxSlider.setPrefWidth(300);
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            controleur.setSfxVolume(value);
            SoundEffects.setVolume(value / 100.0);
        });

        // Plein écran
        Label fullscreenLabel = new Label(I18N.get("fullscreen.label"));
        fullscreenLabel.setFont(Font.font("Arial", 16));
        fullscreenLabel.setTextFill(Color.WHITE);

        ToggleButton fullscreenYes = createToggle("Yes");
        ToggleButton fullscreenNo = createToggle("No");
        ToggleGroup fullscreenGroup = new ToggleGroup();
        fullscreenYes.setToggleGroup(fullscreenGroup);
        fullscreenNo.setToggleGroup(fullscreenGroup);
        if (controleur.isFullscreen()) fullscreenYes.setSelected(true);
        else fullscreenNo.setSelected(true);

        resolutionBox.visibleProperty().bind(fullscreenNo.selectedProperty());
        resolutionBox.managedProperty().bind(fullscreenNo.selectedProperty());

        fullscreenGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                boolean isFullscreen = newToggle == fullscreenYes;
                controleur.setFullscreen(isFullscreen);
                controleur.sauvegarder();
            }
        });

        HBox fullscreenBox = new HBox(10, fullscreenLabel, fullscreenYes, fullscreenNo);
        fullscreenBox.setAlignment(Pos.CENTER);

        // Langue
        Label langueLabel = new Label(I18N.get("language.label"));
        langueLabel.setFont(Font.font("Arial", 16));
        langueLabel.setTextFill(Color.WHITE);

        ToggleButton fr = createToggle("Français");
        ToggleButton en = createToggle("English");
        ToggleButton ja = createToggle("日本語");
        ToggleGroup langGroup = new ToggleGroup();
        fr.setToggleGroup(langGroup);
        en.setToggleGroup(langGroup);
        ja.setToggleGroup(langGroup);
        switch (controleur.getLangue()) {
            case "Français" -> fr.setSelected(true);
            case "English" -> en.setSelected(true);
            case "日本語" -> ja.setSelected(true);
        }

        langGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedLangue = ((ToggleButton) newToggle).getText();
                if (selectedLangue.equals("Français") || selectedLangue.equals("English") || selectedLangue.equals("日本語")) {
                    controleur.setLangue(selectedLangue);
                    controleur.sauvegarder();
                }
            }
        });

        HBox langueBox = new HBox(10, langueLabel, fr, en, ja);
        langueBox.setAlignment(Pos.CENTER);

        // Configuration des touches
        Label touchesLabel = new Label(I18N.get("key.bindings"));
        touchesLabel.setFont(Font.font("Arial", 16));
        touchesLabel.setTextFill(Color.WHITE);

        Button configTouchesBtn = createStyledButton(I18N.get("configure.keys"));
        configTouchesBtn.setOnAction(e -> ouvrirFenetreTouches(controleur));

        // Retour
        Button retour = createStyledButton(I18N.get("back"));
        retour.setOnAction(e -> {
            SoundEffects.play("Benfdal_Croizier/src/assets/audio/return.wav");
            controleur.executerRetour();
        });


        contentBox.getChildren().setAll(
                titre,
                sfxLabel, sfxSlider,
                musicLabel, musicSlider,
                fullscreenBox,
                resolutionBox,
                touchesLabel, configTouchesBtn,
                langueBox,
                retour
        );

        overlayPane.setPickOnBounds(false);
        overlayPane.setMouseTransparent(true);

        this.getChildren().addAll(contentBox, overlayPane);
    }

    /**
     * Ouvre une fenêtre modale pour configurer les touches du clavier.
     *
     * @param controleur le contrôleur à utiliser pour appliquer les modifications de touches
     */
    private void ouvrirFenetreTouches(SettingsControleur controleur) {
        VBox popup = new VBox(15);
        popup.setAlignment(Pos.CENTER);
        popup.setPadding(new Insets(30));
        popup.setStyle("""
    -fx-background-color: white;
    -fx-background-radius: 20;
    -fx-border-width: 2px;
    -fx-border-radius: 20;
    -fx-effect: dropshadow(one-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 4);
""");
        popup.setMaxHeight(Region.USE_PREF_SIZE);
        popup.setPrefHeight(Region.USE_COMPUTED_SIZE);
        popup.setMaxWidth(400);
        popup.setOpacity(0);
        popup.setScaleX(0.8);
        popup.setScaleY(0.8);

        Label title = new Label(I18N.get("keys.title"));
        title.setFont(Font.font("Arial", 20));
        title.setTextFill(Color.web("#333"));

        String[] actions = {"moveUp", "moveDown", "moveLeft", "moveRight"};
        String[] labels = {
                I18N.get("keys.up"),
                I18N.get("keys.down"),
                I18N.get("keys.left"),
                I18N.get("keys.right")
        };

        VBox champBox = new VBox(10);
        champBox.setAlignment(Pos.CENTER_LEFT);

        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            Label label = new Label(labels[i] + " : ");
            label.setTextFill(Color.web("#333"));

            TextField field = new TextField(controleur.getTouche(action));
            field.setEditable(false);
            field.setPrefWidth(100);
            field.setOnKeyPressed(event -> {
                String touche = event.getCode().getName().toUpperCase(Locale.ROOT);
                field.setText(touche);
                controleur.setTouche(action, touche);
            });

            HBox hbox = new HBox(10, label, field);
            hbox.setAlignment(Pos.CENTER_LEFT);
            champBox.getChildren().add(hbox);
        }

        Button resetBtn = new Button(I18N.get("keys.reset"));
        resetBtn.setOnAction(e -> {
            controleur.resetTouchesParDefaut();
            champBox.getChildren().clear();
            for (int i = 0; i < actions.length; i++) {
                String action = actions[i];
                Label label = new Label(labels[i] + " : ");
                label.setTextFill(Color.web("#333"));
                TextField field = new TextField(controleur.getTouche(action));
                field.setEditable(false);
                field.setPrefWidth(100);
                field.setOnKeyPressed(event -> {
                    String touche = event.getCode().getName().toUpperCase(Locale.ROOT);
                    field.setText(touche);
                    controleur.setTouche(action, touche);
                });
                HBox hbox = new HBox(10, label, field);
                hbox.setAlignment(Pos.CENTER_LEFT);
                champBox.getChildren().add(hbox);
            }
        });

        Button closeBtn = new Button(I18N.get("keys.close"));
        closeBtn.setOnAction(e -> {
            overlayPane.setMouseTransparent(true);
            FadeTransition fade = new FadeTransition(Duration.millis(300), popup);
            fade.setFromValue(1.0);
            fade.setToValue(0.0);
            ScaleTransition shrink = new ScaleTransition(Duration.millis(300), popup);
            shrink.setToX(0.8);
            shrink.setToY(0.8);
            ParallelTransition closeAnim = new ParallelTransition(fade, shrink);
            closeAnim.setOnFinished(ev -> overlayPane.getChildren().clear());
            closeAnim.play();
        });

        HBox buttons = new HBox(10, resetBtn, closeBtn);
        buttons.setAlignment(Pos.CENTER);

        popup.getChildren().addAll(title, champBox, buttons);

        overlayPane.setMouseTransparent(false);
        overlayPane.getChildren().setAll(popup);
        StackPane.setAlignment(popup, Pos.CENTER);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), popup);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(300), popup);
        scaleUp.setToX(1.0);
        scaleUp.setToY(1.0);
        new ParallelTransition(fadeIn, scaleUp).play();
    }

    /**
     * Crée un bouton stylisé pour l'interface des paramètres.
     *
     * @param text le libellé du bouton
     * @return un Button JavaFX configuré avec style et effets visuels
     */
    private Button createStyledButton(String text) {
        Button button = new Button(text);
        button.setFont(Font.font("Arial", 16));
        button.setTextFill(Color.web("#333333"));
        button.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 10 30;");
        DropShadow shadow = new DropShadow();
        shadow.setOffsetY(2.0);
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        button.setEffect(shadow);

        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #dddddd; -fx-background-radius: 20; -fx-padding: 10 30;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: white; -fx-background-radius: 20; -fx-padding: 10 30;"));
        return button;
    }

    /**
     * Crée un ToggleButton stylisé pour les choix (ex. plein écran, langue).
     *
     * @param text le libellé du toggle
     * @return un ToggleButton JavaFX configuré avec styles et comportements au survol
     */
    private ToggleButton createToggle(String text) {
        ToggleButton button = new ToggleButton(text);
        button.setFont(Font.font("Arial", 14));
        button.setTextFill(Color.WHITE);
        button.setStyle("-fx-background-color: #444; -fx-background-radius: 10;");
        button.setOnMouseEntered(e -> {
            if (button.isSelected()) {
                button.setStyle("-fx-background-color: #00eaff; -fx-background-radius: 10; -fx-text-fill: black;");
            } else {
                button.setStyle("-fx-background-color: #666; -fx-background-radius: 10;");
            }
        });

        button.setOnMouseExited(e -> {
            if (button.isSelected()) {
                button.setStyle("-fx-background-color: #00eaff; -fx-background-radius: 10; -fx-text-fill: black;");
            } else {
                button.setStyle("-fx-background-color: #444; -fx-background-radius: 10; -fx-text-fill: white;");
            }
        });

        button.selectedProperty().addListener((obs, wasSelected, isNowSelected) -> {
            if (isNowSelected)
                button.setStyle("-fx-background-color: #00eaff; -fx-background-radius: 10; -fx-text-fill: black;");
            else
                button.setStyle("-fx-background-color: #444; -fx-background-radius: 10; -fx-text-fill: white;");
        });
        return button;
    }

    /**
     * Crée la ligne de sélection de résolution (deux choix minimaux).
     *
     * @return un HBox contenant un Label et un ComboBox pour choisir la résolution
     */
    private HBox createResolutionBox() {
                Label lbl = new Label(I18N.get("settings.resolution"));
                lbl.setFont(Font.font("Arial", 16));
                lbl.setTextFill(Color.WHITE);

                        ComboBox<String> combo = new ComboBox<>();
                combo.getItems().addAll("1280x720", "1920x1080");
                combo.setValue(controleur.getResolution());
                combo.setOnAction(e -> {
                        controleur.setResolution(combo.getValue());
                        controleur.sauvegarder();
                    });

                        HBox box = new HBox(10, lbl, combo);
                box.setAlignment(Pos.CENTER);
                return box;
            }
}