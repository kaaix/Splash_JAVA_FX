import Controleurs.Menu.MenuControleur;
import Modeles.settings.SettingsModel;
import javafx.application.Application;
import javafx.scene.input.KeyCombination;
import javafx.stage.Stage;
import utils.I18N;
import utils.MusicPlayer;
import utils.SoundEffects;

public class Main extends Application {
    @Override
    public void start(Stage stage) {
        SettingsModel model = SettingsModel.load();
        I18N.setLangue(model.getLangue());

        MusicPlayer.setVolume(model.getMusicVolume() / 100.0);
        SoundEffects.setVolume(model.getSfxVolume() / 100.0);

        String[] dims = model.getResolution().split("x");
        stage.setWidth(Double.parseDouble(dims[0]));
        stage.setHeight(Double.parseDouble(dims[1]));

        stage.setMinWidth(1280);  // ou 1280 si tu veux interdire toute réduction
        stage.setMinHeight(720); // équivalent 16:9 (960 * 9 / 16)

        stage.setMaxWidth(1920);
        stage.setMaxHeight(1080);

        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH); // Ajoute cette ligne


        // Empêche que le ratio soit modifié : verrouille à 16:9 (au redimensionnement)
        stage.widthProperty().addListener((obs, oldW, newW) -> {
            double expectedHeight = newW.doubleValue() * 9 / 16;
            if (Math.abs(stage.getHeight() - expectedHeight) > 1) {
                stage.setHeight(expectedHeight);
            }
        });

        stage.heightProperty().addListener((obs, oldH, newH) -> {
            double expectedWidth = newH.doubleValue() * 16 / 9;
            if (Math.abs(stage.getWidth() - expectedWidth) > 1) {
                stage.setWidth(expectedWidth);
            }
        });

        stage.setFullScreenExitHint("");
        stage.setOnShown(e -> {
            stage.setFullScreen(model.isFullscreen());
        });

        MenuControleur controleur = new MenuControleur(stage);
        controleur.afficherVue();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
