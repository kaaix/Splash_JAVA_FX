/**
 * Contrôleur de la vue des paramètres.
 * Gère la lecture et la sauvegarde des préférences utilisateur
 * (volume, plein écran, langue, touches, résolution) via SettingsModel,
 * et applique ces réglages à l’application.
 */
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

    /**
     * Initialise le contrôleur des paramètres.
     * Charge les préférences existantes, applique la langue,
     * prépare le fond animé et construit la vue SettingsView.
     *
     * @param stage           la fenêtre principale (Stage) de l’application
     * @param menuControleur  le contrôleur du menu principal pour gérer le retour
     */
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

    /**
     * Retourne le volume de la musique (0–100).
     *
     * @return le niveau de volume musique
     */
    public int getMusicVolume() {
        return model.getMusicVolume();
    }

    /**
     * Modifie et sauvegarde le volume de la musique.
     *
     * @param volume nouveau volume (0–100)
     */
    public void setMusicVolume(int volume) {
        model.setMusicVolume(volume);
        model.save();
        MusicPlayer.setVolume(volume / 100.0);
    }

    /**
     * Retourne le volume des effets sonores (0–100).
     *
     * @return le niveau de volume des SFX
     */
    public int getSfxVolume() {
        return model.getSfxVolume();
    }

    /**
     * Modifie et sauvegarde le volume des effets sonores.
     *
     * @param volume nouveau volume (0–100)
     */
    public void setSfxVolume(int volume) {
        model.setSfxVolume(volume);
        model.save();
        SoundEffects.setVolume(volume / 100.0);
    }


    /**
     * Indique si l’application est en mode plein écran.
     *
     * @return true si plein écran, false sinon
     */
    public boolean isFullscreen() {
        return model.isFullscreen();
    }

    /**
     * Active ou désactive le plein écran et sauvegarde le choix.
     *
     * @param value true pour plein écran, false pour fenêtre
     */
    public void setFullscreen(boolean value) {
        model.setFullscreen(value);
        model.save();

        System.out.println("🖥️ Application du plein écran : " + value);
        stage.setFullScreenExitHint(""); // empêche le message "touche ESC"
        stage.setFullScreen(value);
    }

    /**
     * Retourne la langue actuelle de l’interface.
     *
     * @return nom de la langue (ex. "Français", "English", "日本語")
     */
    public String getLangue() {
        return model.getLangue();
    }

    /**
     * Modifie la langue de l’interface, sauvegarde et recharge la vue.
     *
     * @param value nouveau nom de langue
     */
    public void setLangue(String value) {
        model.setLangue(value);
        model.save();
        I18N.setLangue(value);

        // Recharge proprement la vue pour appliquer la langue
        Vues.Menu.SettingsView nouvelleVue = new Vues.Menu.SettingsView(this);
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(new utils.InkBackground(), nouvelleVue);
        utils.TransitionUtils.fadeToScene(stage, root); // joli fondu
    }

    /**
     * Définit une action à exécuter lors du retour depuis Settings.
     *
     * @param retourAction Runnable à invoquer au retour
     */
    public void setRetourAction(Runnable retourAction) {
        this.retourAction = retourAction;
    }

    /**
     * Exécute l’action de retour configurée (ou retourne au menu par défaut).
     */
    public void executerRetour() {
        retourAction.run();
    }


    /**
     * Retourne la touche assignée à une action donnée.
     *
     * @param action clé de l’action (ex. "moveUp")
     * @return nom de la touche (ex. "W")
     */
    public String getTouche(String action) {
        return model.getTouches().getOrDefault(action, "");
    }

    /**
     * Assigne et sauvegarde une touche à une action.
     *
     * @param action nom de l’action
     * @param touche nom de la touche
     */
    public void setTouche(String action, String touche) {
        model.getTouches().put(action, touche);
        model.save();
        System.out.println("⌨️ Touche [" + action + "] assignée à : " + touche);
    }

    /**
     * Recharge les préférences et retourne au menu principal
     * (ou exécute l’action personnalisée si définie).
     */
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

    /**
     * Sauvegarde immédiatement toutes les préférences courantes.
     */
    public void sauvegarder() {
        model.save();
    }

    /**
     * Retourne la fenêtre (Stage) associée au contrôleur.
     *
     * @return le Stage principal
     */
    public Stage getStage() {
        return stage;
    }

    /**
     * Définit l’action à exécuter après la sauvegarde si on revient ici.
     *
     * @param onRetour Runnable personnalisé
     */
    public void setOnRetour(Runnable onRetour) {
        this.onRetour = onRetour;
    }

    /**
     * Réinitialise les touches aux valeurs par défaut et sauvegarde.
     */
    public void resetTouchesParDefaut() {
        model.resetTouchesParDefaut(); // Appelle la méthode du model
        model.save();
    }

        // === Résolution écran ===
                public String getResolution() {
                return model.getResolution();
            }

                public void setResolution(String resolution) {
                model.setResolution(resolution);
                applyResolution(resolution);
            }

    /** Applique immédiatement la taille de fenêtre à la Stage. */
    private void applyResolution(String resolution) {
        String[] dims = resolution.split("x");
        double w = Double.parseDouble(dims[0]);
        double h = Double.parseDouble(dims[1]);
        stage.setWidth(w);
        stage.setHeight(h);
                                            }
}
