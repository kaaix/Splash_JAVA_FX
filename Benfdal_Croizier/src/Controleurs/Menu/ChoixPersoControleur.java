
package Controleurs.Menu;

import Controleurs.Game.GameControleur;
import Vues.Menu.ChoixPersoView;
import Vues.Menu.SelectSaveMenu;
import Vues.Menu.SplashMenu;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import utils.InkBackground;
import utils.MusicPlayer;
import utils.TransitionUtils;

import java.io.File;

public class ChoixPersoControleur {

    private Stage stage;
    private String fichierSauvegarde;

    public ChoixPersoControleur(Stage stage, String fichierSauvegarde) {
        this.stage = stage;
        this.fichierSauvegarde = fichierSauvegarde;

        ChoixPersoView vue = new ChoixPersoView(this);
        StackPane root = new StackPane(new InkBackground(), vue, vue.getInfoLabel());
        StackPane.setAlignment(vue.getInfoLabel(), Pos.TOP_RIGHT); // 👈 position fixe
        StackPane.setMargin(vue.getInfoLabel(), new Insets(20, 20, 0, 0)); // ⬆️ haut, ➡️ droite

        utils.TransitionUtils.fadeToScene(stage, root);
        stage.setFullScreenExitHint("");
        stage.setFullScreen(true);


    }

    public void demarrerPartie(String nomHero, String nomArme) {
        GameControleur gc = new GameControleur(stage);
        gc.setNomHero(nomHero);
        gc.setNomArme(nomArme);
        gc.initialiserHero(); // crée le héros avec les bons attributs
        gc.setNomFichierSauvegarde(fichierSauvegarde);
        gc.sauvegarderPartie(fichierSauvegarde);
        MusicPlayer.fadeOutAndStop(1.5);
        TransitionUtils.fadeToScene(stage, gc.getVue());
    }

    public void retourMenu() {
        File save1 = new File("save1.bin");
        File save2 = new File("save2.bin");
        File save3 = new File("save3.bin");

        boolean aucuneSaveExiste = !save1.exists() && !save2.exists() && !save3.exists();

        if (aucuneSaveExiste) {
            SplashMenu menu = new SplashMenu(new MenuControleur(stage));
            StackPane root = new StackPane(new utils.InkBackground(), menu);
            utils.TransitionUtils.fadeToScene(stage, root);
        } else {
            SelectSaveMenu menu = new SelectSaveMenu(new MenuControleur(stage));
            StackPane root = new StackPane(new utils.InkBackground(), menu);
            utils.TransitionUtils.fadeToScene(stage, root);
        }
    }

}
