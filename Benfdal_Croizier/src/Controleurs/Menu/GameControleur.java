package Controleurs.Menu;

import Vues.Menu.GameView;
import javafx.stage.Stage;

public class GameControleur {
    private Stage stage;
    private GameView vue;

    public GameControleur(Stage stage) {
        this.stage = stage;
        this.vue = new GameView(this);
    }

    public void afficherVue() {
        // utilisé si tu veux forcer la scène sans transition
        javafx.scene.Scene scene = new javafx.scene.Scene(new javafx.scene.layout.StackPane(vue), 800, 600);
        stage.setScene(scene);
    }

    public GameView getVue() {
        return vue;
    }

    public void quitterJeu() {
        System.out.println("Retour au menu depuis le jeu...");
        MenuControleur menuControleur = new MenuControleur(stage);
        utils.TransitionUtils.fadeToScene(stage, new Vues.Menu.SplashMenu(menuControleur));
    }
}
