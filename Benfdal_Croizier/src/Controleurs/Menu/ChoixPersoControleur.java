/**
 * Contrôleur de la sélection de personnage (nom et arme).
 * Construit la vue ChoixPersoView, gère l’application des préférences
 * (langue, plein écran, résolution) et la navigation vers la partie ou
 * le menu en fonction des sauvegardes existantes.
 */
package Controleurs.Menu;

import Controleurs.Game.GameControleur;
import Modeles.settings.SettingsModel;
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

    /**
     * Initialise le contrôleur de choix de personnage.
     * Monte la vue avec fond animé, applique les paramètres utilisateur
     * et affiche le label d’informations sur le héros.
     *
     * @param stage             la fenêtre (Stage) principale de l’application
     * @param fichierSauvegarde chemin du fichier où la partie sera sauvegardée
     */
    public ChoixPersoControleur(Stage stage, String fichierSauvegarde) {
        this.stage = stage;
        this.fichierSauvegarde = fichierSauvegarde;

        ChoixPersoView vue = new ChoixPersoView(this);
        StackPane root = new StackPane(new InkBackground(), vue, vue.getInfoLabel());
        StackPane.setAlignment(vue.getInfoLabel(), Pos.TOP_RIGHT); // 👈 position fixe
        StackPane.setMargin(vue.getInfoLabel(), new Insets(20, 20, 0, 0)); // ⬆️ haut, ➡️ droite

           utils.TransitionUtils.fadeToScene(stage, root);
           // → Au lieu de forcer le full-screen, on recharge et on applique les settings
                   Modeles.settings.SettingsModel settings = SettingsModel.load();
           utils.I18N.setLangue(settings.getLangue());
           stage.setFullScreenExitHint("");
           stage.setFullScreen(settings.isFullscreen());
           if (!settings.isFullscreen()) {
                   String[] dims = settings.getResolution().split("x");
                   stage.setWidth(Double.parseDouble(dims[0]));
                   stage.setHeight(Double.parseDouble(dims[1]));
           }

    }

    /**
     * Démarre une partie après validation du nom et de l’arme.
     * Initialise le héros avec ses attributs, enregistre immédiatement
     * la partie, puis déclenche la transition vers la vue de jeu.
     *
     * @param nomHero le nom choisi par le joueur
     * @param nomArme le type d’arme/arme profil sélectionné
     */
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

    /**
     * Retourne soit directement au menu principal si aucune sauvegarde
     * n’existe, soit au menu de sélection de sauvegarde.
     * Fait apparaître la vue correspondante avec une transition animée.
     */
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
