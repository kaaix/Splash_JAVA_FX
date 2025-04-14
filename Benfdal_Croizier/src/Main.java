import Controleurs.Menu.MenuControleur;
import Modeles.settings.SettingsModel;
import javafx.application.Application;
import javafx.stage.Stage;
import utils.MusicPlayer;
import utils.SoundEffects;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SettingsModel model = SettingsModel.load(); // ✅ charger les paramètres d'abord

        MusicPlayer.setVolume(model.getMusicVolume() / 100.0);
        SoundEffects.setVolume(model.getSfxVolume() / 100.0);

        stage.setFullScreenExitHint("");            // ✅ cacher le message ESC
        stage.setOnShown(e -> {
            stage.setFullScreen(model.isFullscreen());
        });

        MenuControleur controleur = new MenuControleur(stage);
        controleur.afficherVue();                   // ✅ puis afficher la vue
    }
    public static void main(String[] args) {
        launch(args);
    }
}
