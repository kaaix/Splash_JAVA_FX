package Controleurs.Menu;

import Modeles.settings.SettingsModel;
import Vues.Menu.SettingsView;
import Vues.Menu.SplashMenu;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.I18N;
import utils.InkBackground;
import utils.MusicPlayer;
import utils.SoundEffects;

public class SettingsControleur {
    private Stage stage;
    private Parent vue;
    private MenuControleur menuControleur;
    private SettingsModel model;
    private Runnable retourAction = this::retourMenu;
    private Runnable onRetour; // Action personnalisée à exécuter au retour


    public SettingsControleur(Stage stage, MenuControleur menuControleur) {
        this.stage = stage;
        this.menuControleur = menuControleur;
        this.model = SettingsModel.load();

        // ✅ appliquer la langue dès le départ
        I18N.setLangue(model.getLangue());

        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());

        SettingsView content = new SettingsView(this);
        StackPane contentWrapper = new StackPane(content); // ← pour isoler la VBox
        contentWrapper.setPickOnBounds(false);

        // <- le fond + contenu
        this.vue = new StackPane(fond, contentWrapper);

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
        I18N.setLangue(value);

        // Recharge proprement la vue pour appliquer la langue
        Vues.Menu.SettingsView nouvelleVue = new Vues.Menu.SettingsView(this);
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(new utils.InkBackground(), nouvelleVue);
        utils.TransitionUtils.fadeToScene(stage, root); // joli fondu
    }

    public void setRetourAction(Runnable retourAction) {
        this.retourAction = retourAction;
    }

    public void executerRetour() {
        retourAction.run();
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
        model = SettingsModel.load(); // recharge les paramètres
        I18N.setLangue(model.getLangue());
        MusicPlayer.setVolume(model.getMusicVolume() / 100.0);
        SoundEffects.setVolume(model.getSfxVolume() / 100.0);

        if (onRetour != null) {
            onRetour.run(); // 👈 exécute le comportement personnalisé (ex: retour au jeu)
        } else {
            // 👈 sinon on retourne au menu principal
            stage.setFullScreenExitHint("");
            stage.setFullScreen(model.isFullscreen());

            SplashMenu menu = new SplashMenu(menuControleur);
            StackPane root = menuControleur.creerVueAvecFond(menu);
            utils.TransitionUtils.fadeToScene(stage, root);
        }
    }



    public void sauvegarder() {
        model.save();
    }

    public Stage getStage() {
        return stage;
    }

    public void setOnRetour(Runnable onRetour) {
        this.onRetour = onRetour;
    }

    public void resetTouchesParDefaut() {
        model.resetTouchesParDefaut(); // Appelle la méthode du model
        model.save();
    }

}
