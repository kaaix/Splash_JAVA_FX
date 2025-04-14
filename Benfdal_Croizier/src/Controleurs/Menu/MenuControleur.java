package Controleurs.Menu;

import Controleurs.Menu.SettingsControleur;
import Vues.Menu.SplashMenu;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.TransitionUtils;

public class MenuControleur {
    private Stage stage;

    public MenuControleur(Stage stage) {
        this.stage = stage;
    }

    public void afficherVue() {
        SplashMenu splashMenu = new SplashMenu(this);
        Scene scene = new Scene(new StackPane(splashMenu), 800, 600);
        stage.setScene(scene);
        stage.setTitle("Splash");
        stage.show();
    }

    public void lancerJeu() {
        GameControleur gc = new GameControleur(stage);
        TransitionUtils.fadeToScene(stage, gc.getVue());
    }

    public void ouvrirSettings() {
        SettingsControleur sc = new SettingsControleur(stage, this);
        utils.TransitionUtils.fadeToScene(stage, sc.getVue());
    }
}
