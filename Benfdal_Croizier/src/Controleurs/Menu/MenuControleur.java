package Controleurs.Menu;

import Modeles.core.Game;
import Vues.Menu.SplashMenu;
import javafx.stage.Stage;

public class MenuControleur {

    private SplashMenu vue;
    private Stage stage;

    public MenuControleur(SplashMenu vue, Stage stage) {
        this.vue = vue;
        this.stage = stage;
    }

    public void lancerJeu() {
        System.out.println("Lancement du jeu...");
        stage.close();
        Game game = new Game();
        game.run();
    }

    public void ouvrirParametres() {
        System.out.println("Ouverture des paramètres...");
    }
}
