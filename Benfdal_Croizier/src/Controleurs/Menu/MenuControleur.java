package Controleurs.Menu;

import Modeles.core.Game;
import Vues.Menu.FlashCrashMenu;
import javafx.stage.Stage;

public class MenuControleur {

    private FlashCrashMenu vue;
    private Stage stage;

    public MenuControleur(FlashCrashMenu vue, Stage stage) {
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
