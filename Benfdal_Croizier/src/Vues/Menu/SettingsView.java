package Vues.Menu;

import Controleurs.Menu.SettingsControleur;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SettingsView extends VBox {
    public SettingsView(SettingsControleur controleur) {
        this.setSpacing(30);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(30));
        this.setStyle("-fx-background-color: lightblue;");

        Label titre = new Label("⚙ Paramètres");
        titre.setFont(Font.font("Arial", 26));

        // === Volume ===
        Label volumeLabel = new Label("🔊 Volume");
        volumeLabel.setFont(Font.font("Arial", 18));

        Slider volumeSlider = new Slider(0, 100, controleur.getVolume());
        volumeSlider.setShowTickLabels(true);
        volumeSlider.setShowTickMarks(true);
        volumeSlider.setMajorTickUnit(25);
        volumeSlider.setMinorTickCount(4);
        volumeSlider.setBlockIncrement(5);
        volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            controleur.setVolume(newVal.intValue());
        });

        // === Plein écran ===
        CheckBox fullscreenCheck = new CheckBox("🖥️ Mode plein écran");
        fullscreenCheck.setFont(Font.font("Arial", 16));
        fullscreenCheck.setSelected(controleur.isFullscreen()); // état initial

        Button applyFullscreen = createStyledButton("✅ Appliquer l'affichage");
        applyFullscreen.setOnAction(e -> {
            boolean isSelected = fullscreenCheck.isSelected();
            controleur.setFullscreen(isSelected);
        });



        // === Choix des touches ===
        Label touchesLabel = new Label("⌨️ Touches personnalisées");
        touchesLabel.setFont(Font.font("Arial", 18));
        Button configTouchesBtn = createStyledButton("Configurer les touches...");
        configTouchesBtn.setOnAction(e -> {
            System.out.println("⌨️ Configuration des touches (non implémentée)");
            // TODO : afficher un menu de configuration des touches
        });

        // === Langue ===
        Label langueLabel = new Label("🌍 Langue");
        langueLabel.setFont(Font.font("Arial", 18));
        ComboBox<String> langueCombo = new ComboBox<>();
        langueCombo.getItems().addAll("Français", "English");
        langueCombo.setValue("Français"); // valeur par défaut
        langueCombo.setOnAction(e -> {
            System.out.println("🌍 Langue sélectionnée : " + langueCombo.getValue());
            // TODO : changer de langue
        });

        // === Retour ===
        Button retour = createStyledButton("⬅ Retour");
        retour.setOnAction(e -> controleur.retourMenu());

        this.getChildren().addAll(
                titre,
                volumeLabel, volumeSlider,
                fullscreenCheck,
                touchesLabel, configTouchesBtn,
                langueLabel, langueCombo,
                retour, applyFullscreen
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
}
