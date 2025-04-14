package Vues.Menu;

import Controleurs.Menu.SettingsControleur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.MusicPlayer;
import utils.SoundEffects;

public class SettingsView extends VBox {
    public SettingsView(SettingsControleur controleur) {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: rgba(0,0,0,0.5); -fx-background-radius: 20;");

        this.setMaxWidth(600);
        this.setMaxHeight(800);

        Label titre = new Label("⚙ SETTINGS");
        titre.setFont(Font.font("Orbitron", 28));
        titre.setTextFill(Color.WHITE);

// === Volume musique ===
        Label musicLabel = new Label("🎵 Volume Musique");
        musicLabel.setFont(Font.font("Arial", 16));
        musicLabel.setTextFill(Color.WHITE);
        Slider musicSlider = new Slider(0, 100, controleur.getMusicVolume());
        musicSlider.setShowTickLabels(false);
        musicSlider.setShowTickMarks(false);
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            controleur.setMusicVolume(value);
            MusicPlayer.setVolume(value / 100.0);
        });

// === Volume SFX ===
        Label sfxLabel = new Label("🎧 Volume Effets");
        sfxLabel.setFont(Font.font("Arial", 16));
        sfxLabel.setTextFill(Color.WHITE);
        Slider sfxSlider = new Slider(0, 100, controleur.getSfxVolume());
        sfxSlider.setShowTickLabels(false);
        sfxSlider.setShowTickMarks(false);
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            int value = newVal.intValue();
            controleur.setSfxVolume(value);
            SoundEffects.setVolume(value / 100.0);
        });


// === Plein écran ===
        Label fullscreenLabel = new Label("Fullscreen");
        fullscreenLabel.setFont(Font.font("Arial", 16));
        fullscreenLabel.setTextFill(Color.WHITE);

        ToggleButton fullscreenYes = createToggle("Yes");
        ToggleButton fullscreenNo = createToggle("No");
        ToggleGroup fullscreenGroup = new ToggleGroup();
        fullscreenYes.setToggleGroup(fullscreenGroup);
        fullscreenNo.setToggleGroup(fullscreenGroup);
        if (controleur.isFullscreen()) fullscreenYes.setSelected(true);
        else fullscreenNo.setSelected(true);

        fullscreenGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                boolean isFullscreen = newToggle == fullscreenYes;
                controleur.setFullscreen(isFullscreen);
                controleur.sauvegarder();
            }
        });

        HBox fullscreenBox = new HBox(10, fullscreenLabel, fullscreenYes, fullscreenNo);
        fullscreenBox.setAlignment(Pos.CENTER);

// === Langue ===
        Label langueLabel = new Label("Language");
        langueLabel.setFont(Font.font("Arial", 16));
        langueLabel.setTextFill(Color.WHITE);

        ToggleButton fr = createToggle("Français");
        ToggleButton en = createToggle("English");
        ToggleGroup langGroup = new ToggleGroup();
        fr.setToggleGroup(langGroup);
        en.setToggleGroup(langGroup);
        if (controleur.getLangue().equals("Français")) fr.setSelected(true);
        else en.setSelected(true);

        langGroup.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (newToggle != null) {
                String selectedLangue = ((ToggleButton) newToggle).getText();
                controleur.setLangue(selectedLangue);
                controleur.sauvegarder();
            }
        });

        HBox langueBox = new HBox(10, langueLabel, fr, en);
        langueBox.setAlignment(Pos.CENTER);

// === Config touches ===
        Label touchesLabel = new Label("Key Bindings");
        touchesLabel.setFont(Font.font("Arial", 16));
        touchesLabel.setTextFill(Color.WHITE);

        Button configTouchesBtn = createStyledButton("Configure Keys");
        configTouchesBtn.setOnAction(e -> System.out.println("⌨️ Menu touches pas encore implémenté"));

// === Retour ===
        Button retour = createStyledButton("⬅ Back");
        retour.setOnAction(e -> {
            SoundEffects.play("Benfdal_Croizier/src/assets/audio/return.wav");
            controleur.retourMenu();
        });

        this.getChildren().setAll(
                titre,
                sfxLabel, sfxSlider,musicLabel,musicSlider,
                fullscreenBox,
                touchesLabel, configTouchesBtn,
                langueBox,
                retour
        );


    }

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

}
