package Controleurs.Game;

import Controleurs.Menu.MenuControleur;
import Modeles.game.GameModel;
import Modeles.settings.SettingsModel;
import Vues.game.GameView;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class GameControleur {
    private final Stage stage;
    private final GameView vue;
    private final GameModel model;
    private final Map<String, KeyCode> keyBindings = new HashMap<>();

    public GameControleur(Stage stage) {
        this.stage = stage;
        this.model = new GameModel();
        this.vue = new GameView(model);

        // touches personnalisées
        SettingsModel settings = SettingsModel.load();
        settings.getTouches().forEach((action, keyName) -> {
            try {
                keyBindings.put(action, KeyCode.valueOf(keyName.toUpperCase(Locale.ROOT)));
            } catch (IllegalArgumentException e) {
                System.err.println("❌ Touche invalide : " + keyName);
            }
        });

        vue.setPlayerPosition(model.getPlayerX(), model.getPlayerY());

        vue.setOnKeyPressed(event -> {
            double futurX = model.getPlayerX();
            double futurY = model.getPlayerY();
            double pas = 8;

            if (event.getCode() == keyBindings.get("moveUp")) futurY -= pas;
            else if (event.getCode() == keyBindings.get("moveDown")) futurY += pas;
            else if (event.getCode() == keyBindings.get("moveLeft")) futurX -= pas;
            else if (event.getCode() == keyBindings.get("moveRight")) futurX += pas;

            if (model.peutAller(futurX, futurY)) {
                model.setPlayerPosition(futurX, futurY);
                vue.setPlayerPosition(futurX, futurY);
            }
        });
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
