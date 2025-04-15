package Controleurs.Game;

import Controleurs.Menu.MenuControleur;
import Modeles.game.GameModel;
import Modeles.settings.SettingsModel;
import Vues.game.GameView;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameControleur {
    private final Stage stage;
    private GameModel model;
    private final GameView vue;
    private SettingsModel settings;
    private final Map<String, KeyCode> keyBindings = new HashMap<>();

    public GameControleur(Stage stage) {
        this.stage = stage;
        this.vue = new GameView();

        // Chargement des paramètres (touches personnalisées)
        this.settings = SettingsModel.load();
        settings.getTouches().forEach((action, keyName) -> {
            try {
                keyBindings.put(action, KeyCode.valueOf(keyName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Touche invalide : " + keyName + " pour l’action " + action);
            }
        });

        // Centrage du joueur, puis initialisation du modèle
        vue.centrerPlayerEnBas(() -> {
            double x = vue.getPlayerX();
            double y = vue.getPlayerY();
            this.model = new GameModel(x, y);

            // Contrôle clavier avec touches personnalisées
            vue.setOnKeyPressed(event -> {
                double futurX = model.getPlayerX();
                double futurY = model.getPlayerY();

                KeyCode code = event.getCode();

                if (code == keyBindings.get("moveUp")) futurY -= 10;
                else if (code == keyBindings.get("moveDown")) futurY += 10;
                else if (code == keyBindings.get("moveLeft")) futurX -= 10;
                else if (code == keyBindings.get("moveRight")) futurX += 10;

                if (!vue.detecteCollision(futurX, futurY)) {
                    model.setPlayerPosition(futurX, futurY);
                    vue.setPlayerPosition(futurX, futurY);
                }
            });
        });

        // Obstacles (à compléter selon ta map)
        vue.ajouterObstacle(new Rectangle(0, 100, 1920, 10));
        vue.ajouterObstacle(new Rectangle(600, 0, 10, 1920));
        vue.ajouterObstacle(new Rectangle(1300, 0, 10, 1920));
    }

    public Parent getVue() {
        return vue;
    }

    public void quitterJeu() {
        MenuControleur menuControleur = new MenuControleur(stage);
        Vues.Menu.SplashMenu menu = new Vues.Menu.SplashMenu(menuControleur);
        javafx.scene.layout.StackPane root = menuControleur.creerVueAvecFond(menu);
        utils.TransitionUtils.fadeToScene(stage, root);
    }
}
