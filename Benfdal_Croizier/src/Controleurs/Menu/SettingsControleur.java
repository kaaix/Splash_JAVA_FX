package Controleurs.Menu;

import Modeles.settings.SettingsModel;
import Vues.Menu.SettingsView;
import javafx.scene.Parent;
import javafx.stage.Stage;
import utils.InkBackground;
import utils.MusicPlayer;
import utils.SoundEffects;

public class SettingsControleur {
    private Stage stage;
    private Parent vue;
    private MenuControleur menuControleur;
    private SettingsModel model;

    public SettingsControleur(Stage stage, MenuControleur menuControleur) {
        this.stage = stage;
        this.menuControleur = menuControleur;
        this.model = SettingsModel.load(); // 🔄 Chargement
        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());

        SettingsView content = new SettingsView(this);

        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(fond, content);
        this.vue = root;

    }

    public Parent getVue() {
        return vue;
    }

    // === Volume ===
    public int getMusicVolume() {
        return model.getMusicVolume();
    }

    public void setMusicVolume(int volume) {
        model.setMusicVolume(volume);
        model.save();
        MusicPlayer.setVolume(volume / 100.0);
    }

    public int getSfxVolume() {
        return model.getSfxVolume();
    }

    public void setSfxVolume(int volume) {
        model.setSfxVolume(volume);
        model.save();
        SoundEffects.setVolume(volume / 100.0);
    }


    // === Plein écran ===
    public boolean isFullscreen() {
        return model.isFullscreen();
    }

    public void setFullscreen(boolean value) {
        model.setFullscreen(value);
        model.save();

        System.out.println("🖥️ Application du plein écran : " + value);
        stage.setFullScreenExitHint(""); // empêche le message "touche ESC"
        stage.setFullScreen(value);
    }

    // === Langue ===
    public String getLangue() {
        return model.getLangue();
    }

    public void setLangue(String value) {
        model.setLangue(value);
        model.save();
        System.out.println("🌍 Langue sélectionnée : " + value);
        // TODO : changer les textes si besoin
    }

    // === Touches ===
    public String getTouche(String action) {
        return model.getTouches().getOrDefault(action, "");
    }

    public void setTouche(String action, String touche) {
        model.getTouches().put(action, touche);
        model.save();
        System.out.println("⌨️ Touche [" + action + "] assignée à : " + touche);
    }

    public void retourMenu() {
        stage.setFullScreenExitHint("");
        stage.setFullScreen(model.isFullscreen());

        Vues.Menu.SplashMenu menu = new Vues.Menu.SplashMenu(menuControleur);
        javafx.scene.layout.StackPane root = menuControleur.creerVueAvecFond(menu);
        utils.TransitionUtils.fadeToScene(stage, root);
    }


    public void sauvegarder() {
        model.save();
    }
}
