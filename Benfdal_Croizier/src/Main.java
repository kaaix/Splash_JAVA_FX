import Controleurs.Menu.MenuControleur;
import Modeles.settings.SettingsModel;
import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        MenuControleur controleur = new MenuControleur(stage);
        controleur.afficherVue();

        SettingsModel model = SettingsModel.load();
        stage.setFullScreenExitHint("");

        stage.setOnShown(e -> {
            stage.setFullScreen(model.isFullscreen());
        });

    }

    public static void main(String[] args) {
        launch(args);
    }
}
