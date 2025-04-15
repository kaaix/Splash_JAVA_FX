package Controleurs.Menu;

import Controleurs.Game.GameControleur;
import Modeles.settings.SettingsModel;
import Vues.Menu.SplashMenu;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.InkBackground;
import utils.MusicPlayer;
import utils.TransitionUtils;

public class MenuControleur {
    private Stage stage;
    private SettingsModel model;

    public MenuControleur(Stage stage) {
        this.stage = stage;
        this.model = SettingsModel.load(); // ✅ charge dès le début
    }

    public void afficherVue() {
        SplashMenu splashMenu = new SplashMenu(this);

        // === Fond animé ===
        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());

        SettingsModel model = SettingsModel.load();
        stage.setFullScreenExitHint(""); // cacher le texte ESC
        stage.setFullScreen(model.isFullscreen());

        // === Root avec fond + contenu ===
        StackPane root = new StackPane(fond, splashMenu);
        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.setTitle("Splash");
        stage.show();
    }


    public void jouerMusiqueMenu() {
        MusicPlayer.play("Benfdal_Croizier/src/assets/audio/menu.mp3", true);
    }

    public void lancerJeu() {
        GameControleur gc = new GameControleur(stage);
        TransitionUtils.fadeToScene(stage, gc.getVue());
        MusicPlayer.fadeOutAndStop(1.5); // fondu en 1.5 secondes
    }

    public Stage getStage() {
        return stage;
    }

    public void ouvrirSettings() {
        SettingsControleur sc = new SettingsControleur(stage, this);
        utils.TransitionUtils.fadeToScene(stage, sc.getVue());
    }

    public StackPane creerVueAvecFond(VBox contenu) {
        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());
        return new StackPane(fond, contenu);
    }

}
