package Controleurs.Menu;

import Modeles.settings.SettingsModel;
import Vues.Menu.SettingsView;
import javafx.stage.Stage;

public class SettingsControleur {
    private Stage stage;
    private SettingsView vue;
    private MenuControleur menuControleur;
    private SettingsModel model;

    public SettingsControleur(Stage stage, MenuControleur menuControleur) {
        this.stage = stage;
        this.menuControleur = menuControleur;
        this.model = SettingsModel.load(); // 🔄 Chargement
        this.vue = new SettingsView(this);
    }

    public SettingsView getVue() {
        return vue;
    }

    // === Volume ===
    public int getVolume() {
        return model.getVolume();
    }

    public void setVolume(int value) {
        model.setVolume(value);
        model.save(); // 💾
        System.out.println("🎚 Volume réglé à : " + value + "%");
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
        utils.TransitionUtils.fadeToScene(stage, new Vues.Menu.SplashMenu(menuControleur));
    }
}
