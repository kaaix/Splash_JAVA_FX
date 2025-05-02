/**
 * Contrôleur principal du menu de l’application.
 * Gère l’affichage du SplashMenu, la navigation vers
 * l’éditeur de niveaux, les paramètres, le chargement
 * et la création de parties (sauvegardes), ainsi que
 * l’application des préférences utilisateur.
 */
package Controleurs.Menu;

import Controleurs.Editor.LevelEditorController;
import Controleurs.Game.GameControleur;
import Modeles.save.SaveData;
import Modeles.settings.SettingsModel;
import Vues.Menu.SelectSaveMenu;
import Vues.Menu.SplashMenu;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import utils.I18N;
import utils.InkBackground;
import utils.MusicPlayer;
import utils.TransitionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Objects;

public class MenuControleur {
    private Stage stage;
    private SettingsModel model;

    /**
     * Initialise le contrôleur du menu principal
     * et charge les préférences (langue, plein écran, résolution).
     *
     * @param stage la fenêtre JavaFX principale (Stage)
     */
    public MenuControleur(Stage stage) {
        this.stage = stage;
        this.model = SettingsModel.load(); // ✅ charge dès le début
    }

    /**
     * Affiche la vue de démarrage (SplashMenu) avec fond animé
     * et applique les préférences utilisateur (plein écran, titre).
     */
    public void afficherVue() {
        SplashMenu splashMenu = new SplashMenu(this);

        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());

        stage.setFullScreenExitHint("");
        stage.setFullScreen(this.model.isFullscreen()); // ✅ garde les bons réglages

        StackPane root = new StackPane(fond, splashMenu);
        Scene scene = new Scene(root, 800, 600);

        stage.setScene(scene);
        stage.setTitle("Splash");
        stage.show();
    }

    /**
     * Démarre la lecture en boucle de la musique de menu.
     */
    public void jouerMusiqueMenu() {
        MusicPlayer.play("Benfdal_Croizier/src/assets/audio/menu.mp3", true);
    }

    /**
     * Ouvre une boîte de dialogue pour saisir largeur/hauteur
     * d’un nouveau niveau, puis crée et affiche l’éditeur de niveaux.
     */
    public void ouvrirEditeurNiveau() {
        Dialog<int[]> dialog = new Dialog<>();
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle(I18N.get("editor.new_level.title"));
        dialog.setHeaderText(I18N.get("editor.new_level.header"));

        TextField largeurField = new TextField("30");
        TextField hauteurField = new TextField("17");

        Platform.runLater(largeurField::requestFocus);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.add(new Label(I18N.get("editor.new_level.width")), 0, 0);
        grid.add(largeurField, 1, 0);
        grid.add(new Label(I18N.get("editor.new_level.height")), 0, 1);
        grid.add(hauteurField, 1, 1);
        dialog.getDialogPane().setContent(grid);

        ButtonType createButtonType = new ButtonType(I18N.get("editor.new_level.create"), ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    int width = Integer.parseInt(largeurField.getText());
                    int height = Integer.parseInt(hauteurField.getText());
                    return new int[]{width, height};
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });

        dialog.showAndWait().ifPresent(dims -> {
            int width = dims[0];
            int height = dims[1];

            SettingsModel settings = SettingsModel.load();
            I18N.setLangue(settings.getLangue());
            stage.setFullScreenExitHint("");
            stage.setFullScreen(settings.isFullscreen());

            LevelEditorController lec = new LevelEditorController(stage, this, width, height);
            TransitionUtils.fadeToScene(stage, lec.getVue());
        });
    }

    /**
     * Retourne le Stage associé à ce contrôleur.
     *
     * @return la fenêtre JavaFX principale
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Ouvre la vue des paramètres (SettingsView)
     * avec une transition animée.
     */
    public void ouvrirSettings() {
        SettingsControleur sc = new SettingsControleur(stage, this);
        utils.TransitionUtils.fadeToScene(stage, sc.getVue());
    }

    /**
     * Construit un StackPane combinant un fond animé
     * (InkBackground) et le contenu fourni.
     *
     * @param contenu le VBox à afficher par-dessus du fond
     * @return un StackPane prêt à être positionné dans une scène
     */
    public StackPane creerVueAvecFond(VBox contenu) {
        InkBackground fond = new InkBackground();
        fond.prefWidthProperty().bind(stage.widthProperty());
        fond.prefHeightProperty().bind(stage.heightProperty());
        return new StackPane(fond, contenu);
    }

    /**
     * Charge les données de sauvegarde depuis un fichier,
     * initialise le GameControleur puis lance la partie
     * via fadeToScene.
     *
     * @param fichier chemin vers le fichier de sauvegarde
     */
    public void lancerJeuDepuisSave(String fichier) {
        try {
            FileInputStream fis = new FileInputStream(fichier);
            ObjectInputStream ois = new ObjectInputStream(fis);
            SaveData data = (SaveData) ois.readObject();
            ois.close();
            fis.close();

            GameControleur gc = new GameControleur(stage);
            gc.setNomFichierSauvegarde(fichier); // pour les futurs save
            gc.chargerDepuisSave(data);
            TransitionUtils.fadeToScene(stage, gc.getVue());
            MusicPlayer.fadeOutAndStop(1.5);
        } catch (Exception e) {
            System.err.println("❌ Erreur de chargement de la sauvegarde : " + fichier);
            e.printStackTrace();
        }
    }

    /**
     * Démarre la sélection de personnage pour une nouvelle partie
     * et enregistre le futur fichier de sauvegarde.
     *
     * @param nomFichier nom du fichier où sera enregistrée la partie
     */
    public void lancerNouvellePartie(String nomFichier) {
        ChoixPersoControleur cc = new ChoixPersoControleur(stage, nomFichier);
    }

    /**
     * Lit et désérialise les données de sauvegarde depuis un fichier.
     *
     * @param fichier chemin vers le fichier de sauvegarde
     * @return l’objet SaveData ou null si erreur de lecture
     */
    public SaveData chargerSave(String fichier) {
        try {
            FileInputStream fis = new FileInputStream(fichier);
            ObjectInputStream ois = new ObjectInputStream(fis);
            SaveData data = (SaveData) ois.readObject();
            ois.close();
            fis.close();
            return data;
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la sauvegarde : " + fichier);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Affiche le menu de sélection de sauvegarde si des fichiers
     * existent, sinon lance directement une nouvelle partie.
     */
    public void ouvrirSelectionSauvegarde() {
        File save1 = new File("save1.bin");
        File save2 = new File("save2.bin");
        File save3 = new File("save3.bin");

        boolean aucuneSaveExiste = !save1.exists() && !save2.exists() && !save3.exists();

        if (aucuneSaveExiste) {
            lancerNouvellePartie("save1.bin");
        } else {
            SelectSaveMenu selectSaveMenu = new SelectSaveMenu(this);
            StackPane root = creerVueAvecFond(selectSaveMenu);
            utils.TransitionUtils.fadeToScene(stage, root);


            // ✅ on force le plein écran juste après avoir mis la scène
            stage.setFullScreenExitHint("");
            stage.setFullScreen(this.model.isFullscreen());
        }
    }

    /**
     * Retourne le modèle de préférences chargé.
     *
     * @return l’instance courante de SettingsModel
     */
    public SettingsModel getSettingsModel() {
        return model;
    }

    /**
     * Revient à la vue de démarrage (SplashMenu).
     */
    public void retourSplash() { afficherVue(); }
}